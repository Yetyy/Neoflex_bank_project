package neoflex.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoanOfferDto {
    private UUID statementId;
    private BigDecimal requestedAmount;  // Сумма изначального кредита
    private BigDecimal totalAmount;      // Итоговая сумма кредита (кредит + переплата)
    private Integer term;
    private BigDecimal monthlyPayment;   // Месячный платёж
    private BigDecimal rate;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}
