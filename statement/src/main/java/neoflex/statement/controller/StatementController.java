package neoflex.statement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.statement.service.StatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с заявками на кредит.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/statement")
public class StatementController {

    private static final Logger logger = LoggerFactory.getLogger(StatementController.class);
    private final StatementService statementService;

    /**
     * Обрабатывает запрос на прескоринг и расчет возможных условий кредита.
     *
     * @param requestDto объект с данными заявки на кредит
     * @return список предложений по кредиту
     */
    @PostMapping
    @Operation(summary = "Прескоринг и расчет возможных условий кредита", description = "Отправляет запрос на прескоринг и расчет возможных условий кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод", content = @Content(mediaType = "application/json"))
    })
    public List<LoanOfferDto> getLoanOffers(@RequestBody LoanStatementRequestDto requestDto) {
        logger.info("Получен запрос на кредитную заявку: {}", requestDto);
        List<LoanOfferDto> loanOffers = statementService.getLoanOffers(requestDto);
        logger.info("Возвращаются предложения по кредиту: {}", loanOffers);
        return loanOffers;
    }

    /**
     * Обрабатывает запрос на выбор одного из предложений по кредиту.
     *
     * @param offerDto объект с данными выбранного кредитного предложения
     */
    @PostMapping("/offer")
    @Operation(summary = "Выбор одного из предложений", description = "Выбирает одно из предложений по кредиту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод", content = @Content(mediaType = "application/json"))
    })
    public void selectLoanOffer(@RequestBody LoanOfferDto offerDto) {
        logger.info("Получен запрос на выбор кредитного предложения: {}", offerDto);
        statementService.selectLoanOffer(offerDto);
        logger.info("Кредитное предложение успешно выбрано");
    }
}
