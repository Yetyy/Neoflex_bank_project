/**
 * DTO для кредитного предложения.
 */
package neoflex.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
