package neoflex.deal.entity;

import lombok.*;
import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;

import jakarta.persistence.*;
import java.util.UUID;

import java.math.BigDecimal;

/**
 * Сущность, представляющая информацию о занятости клиента.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "employment")
public class Employment {
    @Id
    @GeneratedValue
    @Column(name = "employment_uid")
    private UUID employmentUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmploymentStatus status;

    @Column(name = "employer_inn")
    private String employerInn;

    @Column(name = "salary")
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private EmploymentPosition position;

    @Column(name = "work_experience_total")
    private Integer workExperienceTotal;

    @Column(name = "work_experience_current")
    private Integer workExperienceCurrent;
}
