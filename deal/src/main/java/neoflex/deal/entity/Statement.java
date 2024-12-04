package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neoflex.deal.converter.StatusHistoryConverter;
import neoflex.deal.enums.ApplicationStatus;
import org.hibernate.annotations.ColumnTransformer;

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

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(read = "applied_offer::jsonb", write = "?::jsonb")
    private String appliedOffer;

    private LocalDateTime signDate;
    private String sesCode;

    @Convert(converter = StatusHistoryConverter.class)
    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(read = "status_history::jsonb", write = "?::jsonb")
    private List<StatusHistory> statusHistory;
}
