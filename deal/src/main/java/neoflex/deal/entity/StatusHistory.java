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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    @Id
    @GeneratedValue
    private UUID statusHistoryId;
    private String status;
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
}
