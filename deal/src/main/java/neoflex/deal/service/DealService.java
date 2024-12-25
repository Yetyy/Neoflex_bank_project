package neoflex.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import neoflex.dto.*;
import neoflex.deal.entity.*;
import neoflex.enums.ApplicationStatus;
import neoflex.enums.ChangeType;
import neoflex.enums.CreditStatus;
import neoflex.deal.mapper.PaymentScheduleElementMapper;
import neoflex.deal.mapper.ScoringDataMapper;
import neoflex.deal.repository.*;
import neoflex.deal.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для обработки бизнес-логики, связанной с кредитными заявками.
 */
@Service
@RequiredArgsConstructor
public class DealService {
    private static final Logger logger = LoggerFactory.getLogger(DealService.class);
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final WebClient webClient;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    /**
     * Рассчитывает возможные условия кредита на основе данных заявки.
     *
     * @param request объект с данными заявки на кредит
     * @return список предложений по кредиту
     */
    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto request) {
        logger.info("Получен запрос на расчет возможных условий кредита: {}", request);

        validateRequest(request);

        Passport passport = createAndSavePassport(request);
        Employment employment = createDefaultEmployment();
        Client client = saveClient(request, passport, employment);
        Statement statement = saveStatement(client);

        List<LoanOfferDto> loanOffers = fetchLoanOffersFromCalculator(request);
        assignStatementIdToLoanOffers(loanOffers, statement.getStatementId());

        logger.info("Предложения по кредиту рассчитаны и связаны с заявкой: {}", loanOffers);

        return loanOffers;
    }

    private void validateRequest(LoanStatementRequestDto request) {
        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            logger.error("Ошибка валидации данных заявки: {}", violations);
            throw new IllegalArgumentException("Invalid loan statement request: " + violations);
        }
    }

    /**
     * Создает и сохраняет паспорт на основе данных запроса.
     *
     * @param request запрос на расчет условий кредита
     * @return сохраненный паспорт
     */
    private Passport createAndSavePassport(LoanStatementRequestDto request) {
        Passport passport = Passport.builder()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .build();
        return passport;
    }

    /**
     * Создает пустой объект Employment.
     *
     * @return пустой объект Employment
     */
    private Employment createDefaultEmployment() {
        return Employment.builder().build();
    }

    /**
     * Сохраняет клиента в базе данных.
     *
     * @param request    запрос на расчет условий кредита
     * @param passport   паспорт клиента
     * @param employment информация о занятости клиента
     * @return сохраненный клиент
     */
    private Client saveClient(LoanStatementRequestDto request, Passport passport, Employment employment) {
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
                .employment(employment)
                .build();
        clientRepository.save(client);
        logger.info("Клиент сохранен: {}", client);
        return client;
    }

    /**
     * Сохраняет заявку в базе данных.
     *
     * @param client клиент, подавший заявку
     * @return сохраненная заявка
     */
    private Statement saveStatement(Client client) {
        Statement statement = Statement.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(List.of())
                .build();
        statementRepository.save(statement);
        logger.info("Заявка сохранена: {}", statement);
        return statement;
    }

    /**
     * Получает предложения по кредиту от микросервиса Калькулятор.
     *
     * @param request запрос на расчет условий кредита
     * @return список предложений по кредиту
     */
    private List<LoanOfferDto> fetchLoanOffersFromCalculator(LoanStatementRequestDto request) {
        try {
            List<LoanOfferDto> loanOffers = webClient.post()
                    .uri("/offers")
                    .body(Mono.just(request), LoanStatementRequestDto.class)
                    .retrieve()
                    .bodyToFlux(LoanOfferDto.class)
                    .collectList()
                    .block();

            logger.info("Получены предложения по кредиту от сервиса Калькулятор: {}", loanOffers);
            return loanOffers;
        } catch (Exception e) {
            logger.error("Ошибка при вызове микросервиса Калькулятор: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении предложений по кредиту", e);
        }
    }

    /**
     * Присваивает ID заявки предложениям по кредиту.
     *
     * @param loanOffers  список предложений по кредиту
     * @param statementId ID заявки
     */
    private void assignStatementIdToLoanOffers(List<LoanOfferDto> loanOffers, UUID statementId) {
        loanOffers.forEach(offer -> offer.setStatementId(statementId));
        logger.info("ID заявки присвоен предложениям: {}", loanOffers);
    }

    /**
     * Выбирает кредитное предложение на основе предоставленного объекта LoanOfferDto.
     *
     * @param offer объект с данными выбранного кредитного предложения
     */
    @Transactional
    public void selectLoanOffer(LoanOfferDto offer) {
        logger.info("Выбор кредитного предложения: {}", offer);

        Statement statement = getStatementById(offer.getStatementId());
        String appliedOfferJson = SerializationUtil.serializeLoanOffer(offer, objectMapper);

        // Десериализация appliedOfferJson в LoanOfferDto
        LoanOfferDto appliedOfferDto = SerializationUtil.deserializeLoanOffer(appliedOfferJson, objectMapper);

        // Обновление поля кредита в заявке информацией из appliedOfferDto
        Credit credit = statement.getCredit();
        if (credit == null) {
            credit = new Credit();
        }
        credit.setAmount(appliedOfferDto.getRequestedAmount());
        credit.setTerm(appliedOfferDto.getTerm());
        credit.setMonthlyPayment(appliedOfferDto.getMonthlyPayment());
        credit.setRate(appliedOfferDto.getRate());
        credit.setInsuranceEnabled(appliedOfferDto.isInsuranceEnabled());
        credit.setSalaryClient(appliedOfferDto.isSalaryClient());

        // Сохранение сущности кредита
        credit = creditRepository.save(credit);

        statement.setCredit(credit);

        updateStatement(statement, appliedOfferJson);
        updateStatusHistory(statement, ApplicationStatus.APPROVED, ChangeType.MANUAL);

        statementRepository.save(statement);
        logger.info("Кредитное предложение успешно выбрано: {}", statement);
    }




    /**
     * Получает заявку по ID.
     *
     * @param statementId ID заявки
     * @return заявка
     */
    private Statement getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка с ID " + statementId + " не найдена"));
    }



    /**
     * Обновляет заявку с новым статусом и примененным предложением.
     *
     * @param statement        заявка
     * @param appliedOfferJson JSON строка примененного предложения
     */
    private void updateStatement(Statement statement, String appliedOfferJson) {
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setAppliedOffer(appliedOfferJson);
        logger.info("Заявка обновлена: {}", statement);
    }

    /**
     * Обновляет историю статусов заявки.
     *
     * @param statement   заявка
     * @param status      новый статус
     * @param changeType  тип изменения
     */
    private void updateStatusHistory(Statement statement, ApplicationStatus status, ChangeType changeType) {
        StatusHistory statusHistory = StatusHistory.builder()
                .status(status.name())
                .time(LocalDateTime.now())
                .changeType(changeType)
                .build();
        List<StatusHistory> statusHistoryList = statement.getStatusHistory();
        statusHistoryList.add(statusHistory);
        statement.setStatusHistory(statusHistoryList);
        logger.info("История статусов обновлена: {}", statusHistoryList);
    }

    /**
     * Завершает регистрацию и выполняет полный подсчет кредита для заявки с указанным идентификатором.
     *
     * @param statementId идентификатор заявки
     * @param request    объект с данными для завершения регистрации
     */
    public void finishRegistration(String statementId, FinishRegistrationRequestDto request) {
        logger.info("Завершение регистрации и полный подсчет кредита для заявки с ID: {}", statementId);

        Statement statement = getStatementById(UUID.fromString(statementId));
        ScoringDataDto scoringData = ScoringDataMapper.toScoringDataDto(statement, request);
        logger.info("Создан запрос для МС Калькулятор: {}", scoringData);

        CreditDto creditDto = sendScoringDataToCalculator(scoringData);
        List<PaymentScheduleElement> paymentScheduleElements = convertPaymentSchedule(creditDto.getPaymentSchedule());

        Credit credit = createAndSaveCredit(creditDto, paymentScheduleElements);
        updateStatementStatus(statement, ApplicationStatus.DOCUMENT_CREATED);

        logger.info("Статус заявки обновлен: {}", statement);
    }

    /**
     * Отправляет данные для скоринга в микросервис Калькулятор.
     *
     * @param scoringData данные для скоринга
     * @return данные кредита
     */
    private CreditDto sendScoringDataToCalculator(ScoringDataDto scoringData) {
        try {
            CreditDto creditDto = webClient.post()
                    .uri("/calc")
                    .body(Mono.just(scoringData), ScoringDataDto.class)
                    .retrieve()
                    .bodyToMono(CreditDto.class)
                    .block();

            logger.info("Получен ответ от МС Калькулятор: {}", creditDto);
            return creditDto;
        } catch (Exception e) {
            logger.error("Ошибка при вызове микросервиса Калькулятор: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении данных кредита", e);
        }
    }

    /**
     * Преобразует элементы графика платежей из DTO в сущности.
     *
     * @param paymentSchedule список элементов графика платежей в формате DTO
     * @return список элементов графика платежей в формате сущностей
     */
    private List<PaymentScheduleElement> convertPaymentSchedule(List<PaymentScheduleElementDto> paymentSchedule) {
        List<PaymentScheduleElement> paymentScheduleElements = PaymentScheduleElementMapper.INSTANCE.toEntities(paymentSchedule);
        logger.info("Преобразованы элементы графика платежей: {}", paymentScheduleElements);
        return paymentScheduleElements;
    }

    /**
     * Создает и сохраняет кредит на основе данных DTO.
     *
     * @param creditDto               данные кредита в формате DTO
     * @param paymentScheduleElements список элементов графика платежей
     * @return сохраненный кредит
     */
    private Credit createAndSaveCredit(CreditDto creditDto, List<PaymentScheduleElement> paymentScheduleElements) {
        String paymentScheduleJson = SerializationUtil.serializePaymentSchedule(paymentScheduleElements, objectMapper);

        Credit credit = Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .monthlyPayment(creditDto.getMonthlyPayment())
                .rate(creditDto.getRate())
                .psk(creditDto.getPsk())
                .paymentSchedule(paymentScheduleJson)
                .insuranceEnabled(creditDto.getIsInsuranceEnabled())
                .salaryClient(creditDto.getIsSalaryClient())
                .creditStatus(CreditStatus.CALCULATED)
                .build();
        creditRepository.save(credit);
        logger.info("Кредит сохранен: {}", credit);
        return credit;
    }


    /**
     * Обновляет статус заявки.
     *
     * @param statement заявка
     * @param status    новый статус
     */
    private void updateStatementStatus(Statement statement, ApplicationStatus status) {
        statement.setStatus(status);
        statementRepository.save(statement);
    }
}
