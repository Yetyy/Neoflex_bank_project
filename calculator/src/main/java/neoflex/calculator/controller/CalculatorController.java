/**
 * Пакет контроллеров для калькулятора кредитных предложений.
 */
package neoflex.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import neoflex.calculator.dto.CreditDto;
import neoflex.calculator.dto.LoanStatementRequestDto;
import neoflex.calculator.dto.LoanOfferDto;
import neoflex.calculator.dto.ScoringDataDto;
import neoflex.calculator.service.LoanOfferService;
import neoflex.calculator.util.AgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с калькулятором кредитных предложений.
 */
@RestController
@RequestMapping("/calculator")
@Tag(name = "Calculator API", description = "API для расчета кредитных предложений и деталей кредита")
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    private final LoanOfferService loanOfferService;

    /**
     * Конструктор для инициализации сервиса кредитных предложений.
     *
     * @param loanOfferService сервис для генерации кредитных предложений
     */
    @Autowired
    public CalculatorController(LoanOfferService loanOfferService) {
        this.loanOfferService = loanOfferService;
    }

    /**
     * Обрабатывает запрос на расчет кредитных предложений.
     *
     * @param request запрос на расчет кредитных предложений
     * @return список кредитных предложений или сообщение об ошибке
     */
    @PostMapping("/offers")
    @Operation(summary = "Расчет кредитных предложений", description = "Расчет кредитных предложений на основе предоставленного запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getLoanOffers(@RequestBody LoanStatementRequestDto request) {
        logger.info("Получен запрос LoanStatementRequestDto: {}", request);

        int age = AgeUtils.calculateAge(request.getBirthDate(), LocalDate.now());
        logger.debug("Рассчитанный возраст клиента: {}", age);

        if (age < 20 || age > 65) {
            logger.warn("Возраст клиента {} не подходит для кредита. Отклонение запроса.", age);
            return ResponseEntity.status(400).body("Отказ: возраст клиента должен быть от 20 до 65 лет.");
        }

        List<LoanOfferDto> loanOffers = loanOfferService.generateLoanOffers(request);
        logger.info("Сгенерировано {} кредитных предложений.", loanOffers.size());
        return ResponseEntity.ok(loanOffers);
    }

    /**
     * Обрабатывает запрос на расчет деталей кредита.
     *
     * @param scoringData данные для скоринга
     * @return детали кредита или сообщение об ошибке
     */
    @PostMapping("/calc")
    @Operation(summary = "Расчет деталей кредита", description = "Расчет деталей кредита на основе предоставленных данных для скоринга")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> calculateCredit(@RequestBody ScoringDataDto scoringData) {
        logger.info("Получены данные для скоринга ScoringDataDto: {}", scoringData);

        try {
            CreditDto creditData = loanOfferService.calculateCredit(scoringData);
            logger.info("Рассчитанные данные кредита CreditDto: {}", creditData);
            return ResponseEntity.ok(creditData);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при расчете кредита: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
