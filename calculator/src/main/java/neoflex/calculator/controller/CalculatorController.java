package neoflex.calculator.controller;

import lombok.RequiredArgsConstructor;
import neoflex.dto.CreditDto;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.dto.ScoringDataDto;
import neoflex.calculator.service.CalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с калькулятором кредитных предложений.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    private final CalculatorService calculatorService;

    /**
     * Обрабатывает запрос на расчет кредитных предложений.
     *
     * @param request объект с данными заявки на расчет кредитных предложений
     * @return список предложений по кредиту
     */
    @PostMapping("/offers")
    public List<LoanOfferDto> calculateLoanOffers(@RequestBody LoanStatementRequestDto request) {
        logger.info("Получен запрос на расчет кредитных предложений: {}", request);
        List<LoanOfferDto> loanOffers = calculatorService.generateLoanOffers(request);
        logger.info("Сгенерировано {} кредитных предложений.", loanOffers.size());
        return loanOffers;
    }

    /**
     * Обрабатывает запрос на расчет деталей кредита.
     *
     * @param scoringData объект с данными для скоринга
     * @return детали кредита
     */
    @PostMapping("/calc")
    public CreditDto calculateCreditDetails(@RequestBody ScoringDataDto scoringData) {
        logger.info("Получены данные для расчета деталей кредита: {}", scoringData);
        CreditDto creditDetails = calculatorService.calculateCredit(scoringData);
        logger.info("Рассчитанные детали кредита: {}", creditDetails);
        return creditDetails;
    }
}
