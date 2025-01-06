package neoflex.deal.controller;

import lombok.RequiredArgsConstructor;
import neoflex.dto.EmailMessage;
import neoflex.dto.FinishRegistrationRequestDto;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.deal.service.DealService;
import neoflex.enums.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с кредитными заявками.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
public class DealController {

    private static final Logger logger = LoggerFactory.getLogger(DealController.class);

    private final DealService dealService;

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    /**
     * Обрабатывает запрос на расчет возможных условий кредита.
     *
     * @param request объект с данными заявки на кредит
     * @return список предложений по кредиту
     */
    @PostMapping("/statement")
    @Operation(summary = "Расчет возможных условий кредита", description = "Рассчитывает возможные условия кредита на основе предоставленного запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public List<LoanOfferDto> calculateLoanOffers(@RequestBody LoanStatementRequestDto request) {
        logger.info("Получен запрос на расчет возможных условий кредита: {}", request);
        return dealService.calculateLoanOffers(request);
    }

    /**
     * Обрабатывает запрос на выбор одного из предложений по кредиту.
     *
     * @param offer объект с данными выбранного предложения
     */
    @PostMapping("/offer/select")
    @Operation(summary = "Выбор предложения по кредиту", description = "Выбирает одно из предложений по кредиту на основе предоставленного запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public void selectLoanOffer(@RequestBody LoanOfferDto offer) {
        logger.info("Получен запрос на выбор предложения по кредиту: {}", offer);
        EmailMessage emailMessage = dealService.selectLoanOffer(offer);
        kafkaTemplate.send("finish-registration", emailMessage);
    }

    /**
     * Обрабатывает запрос на завершение регистрации и полный подсчет кредита.
     *
     * @param statementId идентификатор заявки
     * @param request объект с данными для завершения регистрации
     */
    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Завершение регистрации и полный подсчет кредита", description = "Завершает регистрацию и выполняет полный подсчет кредита на основе предоставленного запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public void finishRegistration(@PathVariable String statementId, @RequestBody FinishRegistrationRequestDto request) {
        logger.info("Получен запрос на завершение регистрации и полный подсчет кредита для заявки с ID: {}", statementId);
        logger.info("Данные запроса: {}", request);
        EmailMessage emailMessage = dealService.finishRegistration(statementId, request);
        kafkaTemplate.send("finish-registration", emailMessage);
    }

    /**
     * Обрабатывает запрос на отправку документов.
     *
     * @param statementId идентификатор заявки
     */
    @PostMapping("/document/{statementId}/send")
    @Operation(summary = "Отправка документов", description = "Отправляет документы для заявки с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public void sendDocuments(@PathVariable String statementId) {
        logger.info("Получен запрос на отправку документов для заявки с ID: {}", statementId);
        EmailMessage emailMessage = dealService.sendDocuments(statementId);
        kafkaTemplate.send("send-documents", emailMessage);
    }

    /**
     * Обрабатывает запрос на подписание документов.
     *
     * @param statementId идентификатор заявки
     */
    @PostMapping("/document/{statementId}/sign")
    @Operation(summary = "Подписание документов", description = "Подписывает документы для заявки с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public void signDocuments(@PathVariable String statementId) {
        logger.info("Получен запрос на подписание документов для заявки с ID: {}", statementId);
        EmailMessage emailMessage = dealService.signDocuments(statementId);
        kafkaTemplate.send("sign-documents", emailMessage);
    }

    /**
     * Обрабатывает запрос на подписание документов с кодом.
     *
     * @param statementId идентификатор заявки
     */
    @PostMapping("/document/{statementId}/code")
    @Operation(summary = "Подписание документов с кодом", description = "Подписывает документы с кодом для заявки с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public void codeDocuments(@PathVariable String statementId) {
        logger.info("Получен запрос на подписание документов с кодом для заявки с ID: {}", statementId);
        EmailMessage emailMessage = dealService.codeDocuments(statementId);
        kafkaTemplate.send("code-documents", emailMessage);
    }
}
