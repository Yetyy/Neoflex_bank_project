package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neoflex.enums.ChangeType;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая историю статусов заявки.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "status_history")
public class StatusHistory {
    @Id
    @GeneratedValue
    @Column(name = "status_history_id")
    private UUID statusHistoryId;

    @Column(name = "status")
    private String status;

    @Column(name = "time")
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    private ChangeType changeType;
}
