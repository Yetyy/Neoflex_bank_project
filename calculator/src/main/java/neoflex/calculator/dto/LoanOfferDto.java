package neoflex.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LoanOfferDto {
    private UUID statementId;
    private BigDecimal requestedAmount;  // Сумма изначального кредита
    private Integer term;
    private BigDecimal annuityMonthlyPayment;   // Аннуитетный платёж
    private BigDecimal differentiatedMonthlyPayment; // Дифференцированный платёж
    private BigDecimal annuityTotalAmount;       // Итоговая сумма аннуитетного кредита (кредит + переплата)
    private BigDecimal differentiatedTotalAmount; // Итоговая сумма дифференцированного кредита (кредит + переплата)
    private BigDecimal rate;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}
