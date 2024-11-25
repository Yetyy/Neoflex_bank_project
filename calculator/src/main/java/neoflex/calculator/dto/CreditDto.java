/**
 * Пакет DTO для калькулятора кредитных предложений.
 */
package neoflex.calculator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO для данных кредита.
 */
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