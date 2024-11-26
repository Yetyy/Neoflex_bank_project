package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

import java.time.LocalDate;

/**
 * Сущность, представляющая паспорт клиента.
 */
@Entity
@Getter
@Setter
public class Passport {
    @Id
    @GeneratedValue
    private UUID passportUid;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
