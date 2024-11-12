package neoflex.calculator.controller;

import neoflex.calculator.dto.CreditDto;
import neoflex.calculator.dto.LoanStatementRequestDto;
import neoflex.calculator.dto.LoanOfferDto;
import neoflex.calculator.dto.ScoringDataDto;
import neoflex.calculator.service.LoanOfferService;
import neoflex.calculator.util.AgeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final LoanOfferService loanOfferService;

    @Autowired
    public CalculatorController(LoanOfferService loanOfferService) {
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")
    public ResponseEntity<?> getLoanOffers(@RequestBody LoanStatementRequestDto request) {
        int age = AgeUtils.calculateAge(request.getBirthDate(), LocalDate.now());

        // Проверка возраста
        if (age < 20 || age > 65) {
            return ResponseEntity.status(400).body("Отказ: возраст клиента должен быть от 20 до 65 лет.");
        }

        // Генерация предложений
        List<LoanOfferDto> loanOffers = loanOfferService.generateLoanOffers(request);

        return ResponseEntity.ok(loanOffers);
    }
    @PostMapping("/calc")
    public ResponseEntity<?> calculateCredit(@RequestBody ScoringDataDto scoringData) {
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        return ResponseEntity.ok(creditData);
    }

}
