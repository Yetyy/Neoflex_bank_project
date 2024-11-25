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
    private BigDecimal monthlyPayment;
    private BigDecimal totalAmount;
    private BigDecimal rate;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}
