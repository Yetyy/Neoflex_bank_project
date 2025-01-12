/**
 * DTO для данных кредита.
 */
package neoflex.dto;

import lombok.*;
import neoflex.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditDto {

    private BigDecimal amount;
    private Integer term;
    private BigDecimal rate;
    private BigDecimal monthlyPayment;
    private BigDecimal psk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElementDto> paymentSchedule;
}