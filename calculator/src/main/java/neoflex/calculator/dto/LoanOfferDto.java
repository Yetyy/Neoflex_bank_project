/**
 * DTO для кредитного предложения.
 */
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
    private BigDecimal requestedAmount;
    private Integer term;
    //Добавлено разделение на Аннуитентный и Дифференцированный платежи
    private BigDecimal annuityMonthlyPayment;
    private BigDecimal differentiatedMonthlyPayment;
    private BigDecimal annuityTotalAmount;
    private BigDecimal differentiatedTotalAmount;
    private BigDecimal rate;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}
