/**
 * DTO для данных завершения регистрации клиента.
 */
package neoflex.deal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import neoflex.deal.enums.Gender;
import neoflex.deal.enums.MaritalStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class FinishRegistrationRequestDto {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private int dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDto employment;
    private String accountNumber;
}
