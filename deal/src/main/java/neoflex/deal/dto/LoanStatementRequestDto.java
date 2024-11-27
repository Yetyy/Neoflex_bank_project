/**
 * DTO для данных заявки на кредит.
 */
package neoflex.deal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import neoflex.deal.enums.Gender;
import neoflex.deal.enums.MaritalStatus;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class LoanStatementRequestDto {

    @Size(min = 2, max = 30, message = "Имя должно содержать от 2 до 30 латинских букв")
    private String firstName;

    @Size(min = 2, max = 30, message = "Фамилия должна содержать от 2 до 30 латинских букв")
    private String lastName;

    @Size(min = 2, max = 30, message = "Отчество должно содержать от 2 до 30 латинских букв")
    private String middleName;

    @NotNull(message = "Дата рождения не может быть пустой")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @Email(message = "Email адрес должен быть корректным")
    private String email;

    @NotNull(message = "Пол не может быть пустым")
    private Gender gender;

    @NotNull(message = "Семейное положение не может быть пустым")
    private MaritalStatus maritalStatus;

    @Min(value = 0, message = "Количество иждивенцев не может быть отрицательным")
    private int dependentAmount;

    @NotNull(message = "Сумма кредита не может быть пустой")
    @Min(value = 20000, message = "Сумма кредита должна быть больше или равна 20000")
    private BigDecimal amount;

    @NotNull(message = "Срок кредита не может быть пустым")
    @Min(value = 6, message = "Срок кредита должен быть больше или равен 6 месяцам")
    private int term;

    @Pattern(regexp = "^[0-9]{4}$", message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;

    @Pattern(regexp = "^[0-9]{6}$", message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;

    private EmploymentDto employment;
    private String passport;
}
