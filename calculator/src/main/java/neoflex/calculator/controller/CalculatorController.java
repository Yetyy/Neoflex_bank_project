package neoflex.calculator.controller;

import neoflex.calculator.dto.LoanStatementRequestDto;
import neoflex.calculator.dto.LoanOfferDto;
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
        // Генерация предложений
        List<LoanOfferDto> loanOffers = loanOfferService.generateLoanOffers(request);

        return ResponseEntity.ok(loanOffers);
    }
}
