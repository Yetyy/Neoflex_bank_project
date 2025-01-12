/**
 * DTO для данных о занятости.
 */
package neoflex.dto;

import lombok.*;
import neoflex.enums.EmploymentStatus;
import neoflex.enums.EmploymentPosition;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {


    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
