package neoflex.calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class CreditDto {
    @Getter
    @Setter
    private BigDecimal amount;
    @Getter
    @Setter
    private Integer term;
    @Getter
    @Setter
    private BigDecimal rate;
    @Getter
    @Setter
    private BigDecimal annuityMonthlyPayment;
    @Getter
    @Setter
    private BigDecimal annuityPsk;
    @Getter
    @Setter
    private BigDecimal differentiatedMonthlyPayment;
    @Getter
    @Setter
    private BigDecimal differentiatedPsk;
    @Getter
    @Setter
    private Boolean isInsuranceEnabled;
    @Getter
    @Setter
    private Boolean isSalaryClient;
    @Getter
    @Setter
    private List<PaymentScheduleElementDto> annuityPaymentSchedule;
    @Getter
    @Setter
    private List<PaymentScheduleElementDto> differentiatedPaymentSchedule;
}
