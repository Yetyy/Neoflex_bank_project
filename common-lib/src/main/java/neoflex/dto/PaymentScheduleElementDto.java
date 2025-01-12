/**
 * DTO для элемента графика платежей.
 */
package neoflex.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleElementDto {
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;
    private BigDecimal debtPayment;
    private BigDecimal remainingDebt;
}
