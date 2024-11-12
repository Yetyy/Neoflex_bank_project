package neoflex.calculator.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;


public class ScoringDataDto {
    @Getter
    private BigDecimal amount;
    @Getter
    private Integer term;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    private String middleName;
    @Getter
    private Gender gender;
    @Getter
    private LocalDate birthdate;
    @Getter
    private String passportSeries;
    @Getter
    private String passportNumber;
    @Getter
    private LocalDate passportIssueDate;
    @Getter
    private String passportIssueBranch;
    @Getter
    private MaritalStatus maritalStatus;
    @Getter
    private Integer dependentAmount;
    @Getter
    private EmploymentDto employment;
    @Getter
    private String accountNumber;
    @Getter
    private Boolean isInsuranceEnabled;
    @Getter
    private Boolean isSalaryClient;

}

