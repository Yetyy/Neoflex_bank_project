package neoflex.calculator.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanStatementRequestDto {
    @Getter
    private BigDecimal amount;
    @Getter
    private  Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    @Getter
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;

}
