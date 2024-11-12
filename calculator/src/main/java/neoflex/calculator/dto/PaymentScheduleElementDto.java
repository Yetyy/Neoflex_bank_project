package neoflex.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
public class PaymentScheduleElementDto {
    @Setter
    private Integer number;
    @Setter
    private LocalDate date;
    @Setter
    private BigDecimal totalPayment;
    @Setter
    private BigDecimal interestPayment;
    @Setter
    private BigDecimal debtPayment;
    @Setter
    private BigDecimal remainingDebt;
}