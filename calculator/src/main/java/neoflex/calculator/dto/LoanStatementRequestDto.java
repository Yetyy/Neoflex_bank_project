/**
 * DTO для запроса на кредитное предложение.
 */
package neoflex.calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanStatementRequestDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;
}
