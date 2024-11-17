/**
 * DTO для данных о занятости.
 */
package neoflex.calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Перечисление статусов занятости.
 */
@Getter
@Setter
public class EmploymentDto {

    public enum EmploymentStatus {
        EMPLOYED,
        UNEMPLOYED,
        SELF_EMPLOYED,
        BUSINESS_OWNER,
        RETIRED
    }

    public enum Position {
        MANAGER,
        DEVELOPER,
        ANALYST,
        OTHER,
        MIDDLE_MANAGER,
        TOP_MANAGER;
    }

    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
