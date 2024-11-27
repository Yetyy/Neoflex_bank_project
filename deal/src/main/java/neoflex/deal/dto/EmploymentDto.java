/**
 * DTO для данных о занятости.
 */
package neoflex.deal.dto;

import lombok.Getter;
import lombok.Setter;
import neoflex.deal.enums.EmploymentStatus;
import neoflex.deal.enums.EmploymentPosition;

import java.math.BigDecimal;

@Getter
@Setter
public class EmploymentDto {


    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
