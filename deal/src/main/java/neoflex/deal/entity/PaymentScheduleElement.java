package neoflex.deal.entity;

import lombok.*;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность, представляющая элемент графика платежей.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payment_schedule_element")
public class PaymentScheduleElement {
    @Id
    @GeneratedValue
    @Column(name = "payment_schedule_element_id")
    private UUID paymentScheduleElementId;

    @Column(name = "number")
    private Integer number;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "total_payment")
    private BigDecimal totalPayment;

    @Column(name = "interest_payment")
    private BigDecimal interestPayment;

    @Column(name = "debt_payment")
    private BigDecimal debtPayment;

    @Column(name = "remaining_debt")
    private BigDecimal remainingDebt;
}
