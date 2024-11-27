package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neoflex.deal.enums.ApplicationStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность, представляющая заявку на кредит.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String appliedOffer;
    private LocalDateTime signDate;
    private String sesCode;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "statement_id")
    private List<StatusHistory> statusHistory;
}
