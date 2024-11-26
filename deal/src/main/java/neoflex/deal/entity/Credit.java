package neoflex.deal.entity;

import lombok.Getter;
import lombok.Setter;
import neoflex.deal.entity.enums.CreditStatus;

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
public class Credit {
    @Id
    @GeneratedValue
    private UUID creditId;
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    @Column(columnDefinition = "jsonb")
    private List<PaymentScheduleElement> paymentSchedule;
    private boolean insuranceEnabled;
    private boolean salaryClient;
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}