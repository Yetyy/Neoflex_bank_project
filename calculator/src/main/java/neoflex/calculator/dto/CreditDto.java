package neoflex.calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class CreditDto {

    private BigDecimal amount;
    private Integer term;
    private BigDecimal rate;
    private BigDecimal annuityMonthlyPayment;
    private BigDecimal annuityPsk;
    private BigDecimal differentiatedMonthlyPayment;
    private BigDecimal differentiatedPsk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElementDto> annuityPaymentSchedule;
    private List<PaymentScheduleElementDto> differentiatedPaymentSchedule;
}
