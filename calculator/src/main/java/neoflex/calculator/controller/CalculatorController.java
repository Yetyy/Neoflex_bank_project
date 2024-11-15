package neoflex.calculator.controller;

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

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    private final LoanOfferService loanOfferService;

    @Autowired
    public CalculatorController(LoanOfferService loanOfferService) {
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")
    public ResponseEntity<?> getLoanOffers(@RequestBody LoanStatementRequestDto request) {
        logger.info("Received LoanStatementRequestDto: {}", request);

        int age = AgeUtils.calculateAge(request.getBirthDate(), LocalDate.now());
        logger.debug("Calculated client age: {}", age);

        if (age < 20 || age > 65) {
            logger.warn("Client age {} is not eligible for a loan. Rejecting request.", age);
            return ResponseEntity.status(400).body("Отказ: возраст клиента должен быть от 20 до 65 лет.");
        }

        List<LoanOfferDto> loanOffers = loanOfferService.generateLoanOffers(request);
        logger.info("Generated {} loan offers.", loanOffers.size());
        return ResponseEntity.ok(loanOffers);
    }

    @PostMapping("/calc")
    public ResponseEntity<?> calculateCredit(@RequestBody ScoringDataDto scoringData) {
        logger.info("Received ScoringDataDto: {}", scoringData);

        try {
            CreditDto creditData = loanOfferService.calculateCredit(scoringData);
            logger.info("Calculated CreditDto: {}", creditData);
            return ResponseEntity.ok(creditData);
        } catch (IllegalArgumentException e) {
            logger.error("Error during credit calculation: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
