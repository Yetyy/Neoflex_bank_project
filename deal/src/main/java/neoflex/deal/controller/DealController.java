package neoflex.deal.controller;
import neoflex.deal.dto.FinishRegistrationRequestDto;
import neoflex.deal.dto.LoanOfferDto;
import neoflex.deal.dto.LoanStatementRequestDto;
import neoflex.deal.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/deal")
public class DealController {

    private static final Logger logger = LoggerFactory.getLogger(DealController.class);

    @Autowired
    private DealService dealService;

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
        dealService.selectLoanOffer(offer);
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
        dealService.finishRegistration(statementId, request);
    }

}
