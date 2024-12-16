/**
 * DTO для данных завершения регистрации клиента.
 */
package neoflex.dto;

import lombok.*;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishRegistrationRequestDto {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private int dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDto employment;
    private String accountNumber;
}
