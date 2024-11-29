package neoflex.deal.service;

import jakarta.validation.ConstraintViolation;
import neoflex.deal.dto.*;
import neoflex.deal.entity.*;
import neoflex.deal.enums.ApplicationStatus;
import neoflex.deal.enums.ChangeType;
import neoflex.deal.enums.CreditStatus;
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
    private StatusHistoryRepository statusHistoryRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private Validator validator;
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

        // Создание и сохранение клиента
        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .gender(request.getGender())
                .maritalStatus(request.getMaritalStatus())
                .dependentAmount(request.getDependentAmount())
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
    public void selectLoanOffer(LoanOfferDto offer) {
        logger.info("Выбор предложения по кредиту: {}", offer);

        // Получение заявки из БД
        Statement statement = statementRepository.findById(offer.getStatementId()).orElseThrow();

        // Обновление статуса заявки
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setAppliedOffer(offer.toString());

        // Обновление истории статусов
        StatusHistory statusHistory = StatusHistory.builder()
                .status(ApplicationStatus.APPROVED.name())
                .time(LocalDateTime.now())
                .changeType(ChangeType.MANUAL)
                .build();
        statusHistoryRepository.save(statusHistory);

        // Сохранение заявки
        statementRepository.save(statement);
    }
                                                            //ADD JAVA-DOC!!
    public void finishRegistration(String statementId, FinishRegistrationRequestDto request) {
        logger.info("Завершение регистрации и полный подсчет кредита для заявки с ID: {}", statementId);

        // Получение заявки из БД
        Statement statement = statementRepository.findById(UUID.fromString(statementId)).orElseThrow();

        // Создание и отправка запроса в МС Калькулятор
        ScoringDataDto scoringData = ScoringDataDto.builder()
                .amount(statement.getCredit().getAmount())
                .term(statement.getCredit().getTerm())
                .firstName(statement.getClient().getFirstName())
                .lastName(statement.getClient().getLastName())
                .middleName(statement.getClient().getMiddleName())
                .gender(statement.getClient().getGender())
                .birthdate(statement.getClient().getBirthDate())
                .passportSeries(statement.getClient().getPassport().getSeries())
                .passportNumber(statement.getClient().getPassport().getNumber())
                .passportIssueDate(statement.getClient().getPassport().getIssueDate())
                .maritalStatus(statement.getClient().getMaritalStatus())
                .dependentAmount(statement.getClient().getDependentAmount())
                .employment(request.getEmployment())
                .accountNumber(statement.getClient().getAccountNumber())
                .isInsuranceEnabled(statement.getCredit().isInsuranceEnabled())
                .isSalaryClient(statement.getCredit().isSalaryClient())
                .build();

        CreditDto creditDto = webClient.post()
                .uri("/calc")
                .body(Mono.just(scoringData), ScoringDataDto.class)
                .retrieve()
                .bodyToMono(CreditDto.class)
                .block();

        // Преобразование списка PaymentScheduleElementDto в список PaymentScheduleElement
        List<PaymentScheduleElement> paymentScheduleElements = DtoConverter.convertToPaymentScheduleElements(creditDto.getPaymentSchedule());

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