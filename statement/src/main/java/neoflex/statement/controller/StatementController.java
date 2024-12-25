package neoflex.statement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.statement.service.StatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statement")
public class StatementController {

    private static final Logger logger = LoggerFactory.getLogger(StatementController.class);


    @Autowired
    private StatementService statementService;

    @PostMapping
    @Operation(summary = "Прескоринг и расчет возможных условий кредита", description = "Отправляет запрос на прескоринг и расчет возможных условий кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод", content = @Content(mediaType = "application/json"))
    })
    public List<LoanOfferDto> getLoanOffers(@RequestBody LoanStatementRequestDto requestDto) {
        logger.info("Received loan statement request: {}", requestDto);
        List<LoanOfferDto> loanOffers = statementService.getLoanOffers(requestDto);
        logger.info("Returning loan offers: {}", loanOffers);
        return loanOffers;
    }

    @PostMapping("/offer")
    @Operation(summary = "Выбор одного из предложений", description = "Выбирает одно из предложений по кредиту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод", content = @Content(mediaType = "application/json"))
    })
    public void selectLoanOffer(@RequestBody LoanOfferDto offerDto) {
        logger.info("Received loan offer selection: {}", offerDto);
        statementService.selectLoanOffer(offerDto);
        logger.info("Loan offer selected successfully");
    }
}
