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

@RestController
@RequestMapping("/calculator")
@Tag(name = "Calculator API", description = "API for calculating loan offers and credit details")
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    private final LoanOfferService loanOfferService;

    @Autowired
    public CalculatorController(LoanOfferService loanOfferService) {
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")
    @Operation(summary = "Calculate loan offers", description = "Calculates loan offers based on the provided loan statement request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Calculate credit details", description = "Calculates credit details based on the provided scoring data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json"))
    })
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
