package neoflex.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
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
import neoflex.deal.util.SesCodeGenerator;
import neoflex.deal.util.SerializationUtil;
import neoflex.enums.Theme;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static Font getFont(float size, int style, BaseColor color) {
        try {
            BaseFont baseFont = BaseFont.createFont(new ClassPathResource("times.ttf").getURL().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style, color);
        } catch (DocumentException | IOException e) {
            logger.error("Ошибка при создании шрифта: {}", e.getMessage());
            return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
        }
    }

    /**
     * Рассчитывает возможные условия кредита на основе данных заявки.
     *
     * @param request объект с данными заявки на кредит
     * @return список предложений по кредиту
     */
    @Transactional
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
                .creationDate(LocalDateTime.now())
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
    public EmailMessage selectLoanOffer(LoanOfferDto offer) {
        logger.info("Выбор кредитного предложения: {}", offer);

        Statement statement = getStatementById(offer.getStatementId());
        String appliedOfferJson = SerializationUtil.serializeLoanOffer(offer, objectMapper);

        LoanOfferDto appliedOfferDto = SerializationUtil.deserializeLoanOffer(appliedOfferJson, objectMapper);

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

        credit = creditRepository.save(credit);

        statement.setCredit(credit);

        updateStatement(statement, appliedOfferJson);
        updateStatusHistory(statement, ApplicationStatus.APPROVED, ChangeType.MANUAL);

        statementRepository.save(statement);
        logger.info("Кредитное предложение успешно выбрано: {}", statement);

        return new EmailMessage(statement.getStatementId(), Theme.FINISH_REGISTRATION, statement.getClient().getEmail());
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
    @Transactional
    public EmailMessage finishRegistration(String statementId, FinishRegistrationRequestDto request) {
        logger.info("Завершение регистрации и полный подсчет кредита для заявки с ID: {}", statementId);

        Statement statement = getStatementById(UUID.fromString(statementId));
        ScoringDataDto scoringData = ScoringDataMapper.toScoringDataDto(statement, request);
        logger.info("Создан запрос для МС Калькулятор: {}", scoringData);

        CreditDto creditDto;
        try {
            creditDto = sendScoringDataToCalculator(scoringData);
        } catch (RuntimeException e) {
            logger.error("Ошибка при получении данных кредита: {}", e.getMessage());
            updateStatementStatus(statement, ApplicationStatus.CC_DENIED);
            return new EmailMessage(statement.getStatementId(), Theme.STATEMENT_DENIED, statement.getClient().getEmail());
        }
        List<PaymentScheduleElement> paymentScheduleElements = convertPaymentSchedule(creditDto.getPaymentSchedule());

        Credit credit = createAndSaveCredit(creditDto, paymentScheduleElements);
        statement.setCredit(credit);
        updateStatementStatus(statement, ApplicationStatus.CC_APPROVED);

        logger.info("Статус заявки обновлен: {}", statement);

        return new EmailMessage(statement.getStatementId(), Theme.CREATE_DOCUMENTS, statement.getClient().getEmail());
    }

    /**
     * Генерирует PDF документ на основе информации из объекта Statement.
     *
     * @param statement объект Statement, содержащий информацию о клиенте и кредите
     * @return массив байтов, представляющий сгенерированный PDF документ
     * @throws RuntimeException если происходит ошибка при создании PDF документа
     */
    private byte[] generatePdfDocument(Statement statement) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font boldFont = getFont(16, Font.BOLD, BaseColor.BLACK);
            Font headerFont = getFont(12, Font.BOLD, BaseColor.BLACK);
            Font regularFont = getFont(12, Font.NORMAL, BaseColor.BLACK);

            // Информация о клиенте
            Client client = statement.getClient();
            String fullName = getFullName(client);
            Passport passport = client.getPassport();

            addParagraph(document, "Кредитный договор", boldFont);
            addParagraph(document, " ", regularFont);
            addParagraph(document, "Информация о клиенте:", headerFont);
            addParagraph(document, "ФИО: " + fullName, regularFont);
            addParagraph(document, "Дата рождения: " + client.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), regularFont);
            addParagraph(document, "Email: " + client.getEmail(), regularFont);
            addParagraph(document, "Паспортные данные:", headerFont);
            addParagraph(document, "Серия: " + passport.getSeries(), regularFont);
            addParagraph(document, "Номер: " + passport.getNumber(), regularFont);
            if (passport.getIssueDate() != null) {
                addParagraph(document, "Дата выдачи: " + passport.getIssueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), regularFont);
            }
            if (passport.getIssueBranch() != null) {
                addParagraph(document, "Кем выдан: " + passport.getIssueBranch(), regularFont);
            }
            addParagraph(document, " ", regularFont);

            // Информация о кредите
            Credit credit = statement.getCredit();
            addParagraph(document, "Информация о кредите:", headerFont);
            addParagraph(document, "Сумма кредита: " + credit.getAmount(), regularFont);
            addParagraph(document, "Срок кредита: " + credit.getTerm() + " месяцев", regularFont);
            addParagraph(document, "Ежемесячный платеж: " + credit.getMonthlyPayment(), regularFont);
            BigDecimal rate = statement.getCredit().getRate();
            BigDecimal ratePercentage = rate.multiply(new BigDecimal(100));
            addParagraph(document, "Процентная ставка: " + ratePercentage + "%", regularFont);
            addParagraph(document, "ПСК: " + credit.getPsk(), regularFont);
            addParagraph(document, " ", regularFont);

            // График платежей
            List<PaymentScheduleElement> paymentSchedule = SerializationUtil.deserializePaymentSchedule(credit.getPaymentSchedule(), objectMapper);
            if (paymentSchedule != null && !paymentSchedule.isEmpty()) {
                addParagraph(document, "График платежей:", headerFont);
                for (PaymentScheduleElement payment : paymentSchedule) {
                    addParagraph(document, "Номер платежа: " + payment.getNumber() +
                            ", Дата: " + payment.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                            ", Сумма: " + payment.getTotalPayment() +
                            ", Погашение процентов: " + payment.getInterestPayment() +
                            ", Погашение основного долга: " + payment.getDebtPayment() +
                            ", Остаток долга: " + payment.getRemainingDebt(), regularFont);
                }
            } else {
                addParagraph(document, "График платежей отсутствует.", regularFont);
            }
            addParagraph(document, " ", regularFont);

            // Дата создания заявки
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);
            addParagraph(document, "Дата создания заявки: " + formattedDate, regularFont);

            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException e) {
            logger.error("Ошибка при создании PDF документа: {}", e.getMessage());
            throw new RuntimeException("Ошибка при создании PDF документа", e);
        }
    }



    private void addParagraph(Document document, String text, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        document.add(paragraph);
    }

    private String getFullName(Client client) {
        return client.getLastName() + " " + client.getFirstName()  + " " + client.getMiddleName();
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
        List<PaymentScheduleElement> paymentScheduleElements = PaymentScheduleElementMapper.toEntities(paymentSchedule);
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
     * Отправляет документы для заявки с указанным идентификатором.
     *
     * @param statementId идентификатор заявки
     * @return объект Statement с информацией для отправки email
     */
    @Transactional
    public EmailMessage sendDocuments(String statementId) {
        Statement statement = getStatementById(UUID.fromString(statementId));
        updateStatementStatus(statement, ApplicationStatus.PREPARE_DOCUMENTS);
        byte[] pdfBytes = generatePdfDocument(statement);
        return new EmailMessage(statement.getStatementId(), Theme.SEND_DOCUMENTS, statement.getClient().getEmail(), pdfBytes);
    }

    /**
     * Подписывает документы для заявки с указанным идентификатором.
     *
     * @param statementId идентификатор заявки
     * @return объект EmailMessage с информацией для отправки email
     */
    @Transactional
    public EmailMessage signDocuments(String statementId) {
        logger.info("Подписание документов для заявки с ID: {}", statementId);
        Statement statement = getStatementById(UUID.fromString(statementId));

        String sesCode = SesCodeGenerator.generateSesCode();
        logger.debug("Сгенерирован SES код: {} для заявки с ID: {}", sesCode, statementId);

        statement.setSesCode(sesCode);
        statement.setSignDate(LocalDateTime.now());
        statementRepository.save(statement);
        logger.info("SES код сохранен в заявке с ID: {}", statementId);

        EmailMessage emailMessage = new EmailMessage(statement.getStatementId(), Theme.SEND_SES, statement.getClient().getEmail());
        emailMessage.setText("Потвердите согласие на оформление кредита с помощью кода: " + sesCode);
        return emailMessage;
    }

    /**
     * Кодирует документы для заявки с указанным идентификатором.
     *
     * @param statementId идентификатор заявки
     * @param sesCode     код подтверждения
     * @return объект EmailMessage с информацией для отправки email
     */
    @Transactional
    public EmailMessage codeDocuments(String statementId, String sesCode) {
        logger.info("Проверка SES кода для заявки с ID: {}", statementId);
        Statement statement = getStatementById(UUID.fromString(statementId));
        if (!StringUtils.equals(statement.getSesCode(), sesCode)) {
            logger.error("Неверный SES код для заявки с ID: {}", statementId);
            throw new IllegalArgumentException("Неверный SES код");
        }

        logger.info("SES код верный, заявка с ID: {} подтверждена", statementId);
        updateStatementStatus(statement, ApplicationStatus.DOCUMENT_SIGNED);
        return new EmailMessage(statement.getStatementId(), Theme.CREDIT_ISSUED, statement.getClient().getEmail());
    }

    /**
     * Обрабатывает успешную отправку сообщения в Kafka для документов.
     *
     * @param statementId идентификатор заявки
     */
    public void handleKafkaDocumentSuccess(String statementId) {
        Statement statement = getStatementById(UUID.fromString(statementId));
        updateStatementStatus(statement, ApplicationStatus.DOCUMENT_CREATED);
    }

    /**
     * Обрабатывает ошибку при отправке сообщения в Kafka для документов.
     *
     * @param statementId идентификатор заявки
     * @param ex          исключение
     */
    public void handleKafkaDocumentFailure(String statementId, Throwable ex) {
        logger.error("Ошибка при отправке сообщения в Kafka для заявки с ID {}: {}", statementId, ex.getMessage());
    }

    /**
     * Обрабатывает успешную отправку сообщения в Kafka для кредита.
     *
     * @param statementId идентификатор заявки
     */
    public void handleKafkaCreditSuccess(String statementId) {
        Statement statement = getStatementById(UUID.fromString(statementId));
        updateStatementStatus(statement, ApplicationStatus.CREDIT_ISSUED);
        Credit credit = statement.getCredit();
        updateCreditStatus(credit, CreditStatus.ISSUED);
    }

    /**
     * Обрабатывает ошибку при отправке сообщения в Kafka для кредита.
     *
     * @param statementId идентификатор заявки
     * @param ex          исключение
     */
    public void handleKafkaCreditFailure(String statementId, Throwable ex) {
        logger.error("Ошибка при отправке сообщения в Kafka для заявки с ID {}: {}", statementId, ex.getMessage());
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

    /**
     * Обновляет статус кредита.
     *
     * @param credit       кредит для обновления
     * @param creditStatus новый статус кредита
     */
    private void updateCreditStatus(Credit credit, CreditStatus creditStatus) {
        credit.setCreditStatus(creditStatus);
        creditRepository.save(credit);
    }
}
