package neoflex.deal.entity;

import lombok.*;
import neoflex.deal.enums.EmploymentPosition;
import neoflex.deal.enums.EmploymentStatus;

import jakarta.persistence.*;
import java.util.UUID;

import java.math.BigDecimal;


/**
 * Сущность, представляющая информацию о занятости клиента.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employment {
    @Id
    @GeneratedValue
    private UUID employmentUid;
    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;
    private String employerInn;
    private BigDecimal salary;
    @Enumerated(EnumType.STRING)
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
