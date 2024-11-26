package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import neoflex.deal.entity.enums.ApplicationStatus;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность, представляющая заявку на кредит.
 */
@Entity
@Getter
@Setter
public class Statement {
    @Id
    @GeneratedValue
    private UUID statementId;
    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "clientId")
    private Client client;
    @OneToOne
    @JoinColumn(name = "credit_id", referencedColumnName = "creditId")
    private Credit credit;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDateTime creationDate;
    @Column(columnDefinition = "jsonb")
    private String appliedOffer;
    private LocalDateTime signDate;
    private String sesCode;
    @Column(columnDefinition = "jsonb")
    private List<StatusHistory> statusHistory;
}
