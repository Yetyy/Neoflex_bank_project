package neoflex.deal.entity;

import lombok.*;
import neoflex.enums.CreditStatus;

import jakarta.persistence.*;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сущность, представляющая кредит.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "credit")
public class Credit {
    @Id
    @GeneratedValue
    @Column(name = "credit_id")
    private UUID creditId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "term")
    private int term;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "psk")
    private BigDecimal psk;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "credit_id")
    private List<PaymentScheduleElement> paymentSchedule;

    @Column(name = "insurance_enabled")
    private boolean insuranceEnabled;

    @Column(name = "salary_client")
    private boolean salaryClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status")
    private CreditStatus creditStatus;
}