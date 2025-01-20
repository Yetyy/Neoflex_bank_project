package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import java.time.LocalDate;

/**
 * Сущность, представляющая паспорт клиента.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "passport")
public class Passport {
    @Id
    @GeneratedValue
    @Column(name = "passport_uid")
    private UUID passportUid;

    @Column(name = "series")
    private String series;

    @Column(name = "number")
    private String number;

    @Column(name = "issue_branch")
    private String issueBranch;

    @Column(name = "issue_date")
    private LocalDate issueDate;
}
