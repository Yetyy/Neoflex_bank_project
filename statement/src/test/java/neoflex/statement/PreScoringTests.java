package neoflex.statement;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PreScoringTests {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidLoanStatementRequestDto() {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"J", "VeryLongFirstNameeeeeeeeeeeeeeeeeeeeeeeeeeeee"})
    public void testInvalidFirstName(String firstName) {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName(firstName)
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Имя должно содержать от 2 до 30 латинских букв", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"D", "VeryLongLastNameeeeeeeeeeeeeeeeeeeeeeeeeeeee"})
    public void testInvalidLastName(String lastName) {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName(lastName)
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Фамилия должна содержать от 2 до 30 латинских букв", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"S", "VeryLongMiddleNameeeeeeeeeeeeeeeeeeeeeeeeeeeee"})
    public void testInvalidMiddleName(String middleName) {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName(middleName)
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Отчество должно содержать от 2 до 30 латинских букв", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidBirthDate() {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.now().plusDays(1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения должна быть в прошлом", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail() {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Email адрес должен быть корректным", violations.iterator().next().getMessage());
    }


    @Test
    public void testInvalidAmount() {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(19999))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Сумма кредита должна быть больше или равна 20000", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidTerm() {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(5)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Срок кредита должен быть больше или равен 6 месяцам", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "12345"})
    public void testInvalidPassportSeries(String passportSeries) {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries(passportSeries)
                .passportNumber("567890")
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Серия паспорта должна содержать 4 цифры", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"56789", "5678901"})
    public void testInvalidPassportNumber(String passportNumber) {
        LoanStatementRequestDto requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber(passportNumber)
                .build();

        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size());
        assertEquals("Номер паспорта должен содержать 6 цифр", violations.iterator().next().getMessage());
    }
}
