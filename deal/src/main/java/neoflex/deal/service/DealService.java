package neoflex.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import neoflex.deal.dto.*;
import neoflex.deal.entity.*;
import neoflex.deal.enums.ApplicationStatus;
import neoflex.deal.enums.ChangeType;
import neoflex.deal.enums.CreditStatus;
import neoflex.deal.mapper.PaymentScheduleElementMapper;
import neoflex.deal.mapper.ScoringDataMapper;
import neoflex.deal.repository.*;
import neoflex.deal.util.DtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для обработки бизнес-логики, связанной с кредитными заявками.
 */
@Service
public class DealService {
    private static final Logger logger = LoggerFactory.getLogger(DealService.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private PassportRepository passportRepository;

    @Autowired
    private StatusHistoryRepository statusHistoryRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private Validator validator;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * Рассчитывает возможные условия кредита на основе данных заявки.
     *
     * @param request объект с данными заявки на кредит
     * @return список предложений по кредиту
     */
    public List<LoanOfferDto> calculateLoanOffers(@Valid LoanStatementRequestDto request) {
        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Invalid loan statement request: " + violations);
        }
        logger.info("Расчет возможных условий кредита для заявки: {}", request);

        // Создание и сохранение паспорта
        Passport passport = Passport.builder()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .build();
        passportRepository.save(passport);
        //Заглушка так как Employment будет добавляться в МС statement

        // Создание и сохранение клиента с использованием маппера
        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .gender(request.getGender())
                .maritalStatus(request.getMaritalStatus())
                .dependentAmount(request.getDependentAmount())
                .passport(passport)
                .build();
        clientRepository.save(client);
        // Создание и сохранение заявки
        Statement statement = Statement.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .build();
        statementRepository.save(statement);
        List<LoanOfferDto> loanOffers = webClient.post()
                .uri("/offers")
                .body(Mono.just(request), LoanStatementRequestDto.class)
                .retrieve()
                .bodyToFlux(LoanOfferDto.class)
                .collectList()
                .block();
        // Присвоение id заявки каждому предложению
        loanOffers.forEach(offer -> offer.setStatementId(statement.getStatementId()));

        return loanOffers;
    }


    /**
     * Выбирает одно из предложений по кредиту.
     *
     * @param offer объект с данными выбранного предложения
     */
    @Transactional
    public void selectLoanOffer(LoanOfferDto offer) {
        UUID statementId = offer.getStatementId();
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new IllegalArgumentException("Statement not found"));

        // Сериализация LoanOfferDto в строку JSON
        String appliedOfferJson;
        try {
            appliedOfferJson = objectMapper.writeValueAsString(offer);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing LoanOfferDto to JSON", e);
        }

        // Обновление заявки
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setAppliedOffer(appliedOfferJson);

        // Обновление истории статусов
        StatusHistory statusHistory = StatusHistory.builder()
                .status(ApplicationStatus.APPROVED.name())
                .time(LocalDateTime.now())
                .changeType(ChangeType.MANUAL)
                .build();
        List<StatusHistory> statusHistoryList = statement.getStatusHistory();
        statusHistoryList.add(statusHistory);
        statement.setStatusHistory(statusHistoryList);

        // Сохранение заявки
        statementRepository.save(statement);
    }
    /**
     * Завершает регистрацию и выполняет полный подсчет кредита для заявки с указанным идентификатором.
     *
     * @param statementId идентификатор заявки
     * @param request объект с данными для завершения регистрации
     */
    public void finishRegistration(String statementId, FinishRegistrationRequestDto request) {
        logger.info("Завершение регистрации и полный подсчет кредита для заявки с ID: {}", statementId);

        // Получение заявки из БД
        Statement statement = statementRepository.findById(UUID.fromString(statementId)).orElseThrow();

        // Создание и отправка запроса в МС Калькулятор
        ScoringDataDto scoringData = ScoringDataMapper.toScoringDataDto(statement, request);

        CreditDto creditDto = webClient.post()
                .uri("/calc")
                .body(Mono.just(scoringData), ScoringDataDto.class)
                .retrieve()
                .bodyToMono(CreditDto.class)
                .block();

        // Преобразование списка PaymentScheduleElementDto в список PaymentScheduleElement
        List<PaymentScheduleElement> paymentScheduleElements = PaymentScheduleElementMapper.INSTANCE.toEntities(creditDto.getPaymentSchedule());

        // Создание и сохранение кредита
        Credit credit = Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .monthlyPayment(creditDto.getMonthlyPayment())
                .rate(creditDto.getRate())
                .psk(creditDto.getPsk())
                .paymentSchedule(paymentScheduleElements)
                .insuranceEnabled(creditDto.getIsInsuranceEnabled())
                .salaryClient(creditDto.getIsSalaryClient())
                .creditStatus(CreditStatus.CALCULATED)
                .build();
        creditRepository.save(credit);

        // Обновление статуса заявки
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statementRepository.save(statement);
    }
}