/**
 * DTO для данных кредита.
 */
package neoflex.deal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import neoflex.deal.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@Builder
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