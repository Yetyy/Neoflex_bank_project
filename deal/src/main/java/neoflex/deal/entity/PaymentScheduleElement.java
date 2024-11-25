package neoflex.deal.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность, представляющая элемент графика платежей.
 */
@Entity
@Getter
@Setter
public class PaymentScheduleElement {
    @Id
    @GeneratedValue
    private UUID paymentScheduleElementId;
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;
    private BigDecimal debtPayment;
    private BigDecimal remainingDebt;
}
