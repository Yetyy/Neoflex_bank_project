/**
 * DTO для данных о занятости.
 */
package neoflex.calculator.dto;

import lombok.Getter;
import lombok.Setter;
import neoflex.calculator.dto.enums.EmploymentStatus;
import neoflex.calculator.dto.enums.Position;

import java.math.BigDecimal;

@Getter
@Setter
public class EmploymentDto {


    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
