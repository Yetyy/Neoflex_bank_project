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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    @Id
    @GeneratedValue
    private UUID creditId;
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "credit_id")
    private List<PaymentScheduleElement> paymentSchedule;
    private boolean insuranceEnabled;
    private boolean salaryClient;
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}
