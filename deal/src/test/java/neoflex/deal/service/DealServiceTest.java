package neoflex.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.deal.entity.*;
import neoflex.dto.*;
import neoflex.enums.*;
import neoflex.deal.repository.*;
import neoflex.deal.util.SesCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DealServiceTest {


    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;


    @InjectMocks
    private DealService dealService;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;
    private Statement statement;
    private Client client;
    private Credit credit;
    private Passport passport;
    private Employment employment;

    @BeforeEach
    void setUp() {
        passport = Passport.builder()
                .series("1234")
                .number("567890")
                .build();
        employment = Employment.builder().build();
        client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .passport(passport)
                .employment(employment)
                .build();
        credit = Credit.builder()
                .amount(BigDecimal.valueOf(20000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(1000))
                .rate(BigDecimal.valueOf(0.05))
                .build();
        statement = Statement.builder()
                .statementId(UUID.randomUUID())
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(List.of())
                .creationDate(LocalDateTime.now())
                .build();
        finishRegistrationRequestDto = FinishRegistrationRequestDto.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .passportIssueDate(LocalDate.of(2020, 1, 1))
                .passportIssueBranch("Branch1")
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerINN("1234567890")
                        .salary(BigDecimal.valueOf(50000))
                        .position(EmploymentPosition.WORKER)
                        .workExperienceTotal(5)
                        .workExperienceCurrent(3)
                        .build())
                .accountNumber("1234567890")
                .build();
    }

    @Test
    void signDocumentsShouldUpdateStatementAndReturnEmailMessage() {
        when(statementRepository.findById(statement.getStatementId())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        EmailMessage result = dealService.signDocuments(statement.getStatementId().toString());

        assertNotNull(result);
        assertEquals(Theme.SEND_SES, result.getTheme());
        assertEquals(statement.getClient().getEmail(), result.getAddress());
        assertNotNull(result.getText());
        assertTrue(result.getText().contains("Потвердите согласие на оформление кредита с помощью кода:"));
        verify(statementRepository, times(1)).findById(statement.getStatementId());
        verify(statementRepository, times(1)).save(any(Statement.class));
    }

    @Test
    void codeDocumentsShouldUpdateStatementAndReturnEmailMessage() {
        String sesCode = SesCodeGenerator.generateSesCode();
        statement.setSesCode(sesCode);
        when(statementRepository.findById(statement.getStatementId())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        EmailMessage result = dealService.codeDocuments(statement.getStatementId().toString(), sesCode);

        assertNotNull(result);
        assertEquals(Theme.CREDIT_ISSUED, result.getTheme());
        assertEquals(statement.getClient().getEmail(), result.getAddress());
        verify(statementRepository, times(1)).findById(statement.getStatementId());
        verify(statementRepository, times(1)).save(any(Statement.class));
    }

    @Test
    void codeDocumentsShouldThrowExceptionWhenSesCodeIsInvalid() {
        String sesCode = SesCodeGenerator.generateSesCode();
        statement.setSesCode(sesCode);
        when(statementRepository.findById(statement.getStatementId())).thenReturn(Optional.of(statement));

        assertThrows(IllegalArgumentException.class, () -> dealService.codeDocuments(statement.getStatementId().toString(), "invalid"));
        verify(statementRepository, times(1)).findById(statement.getStatementId());
        verify(statementRepository, never()).save(any(Statement.class));
    }

    @Test
    void handleKafkaDocumentSuccessShouldUpdateStatementStatus() {
        when(statementRepository.findById(statement.getStatementId())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        dealService.handleKafkaDocumentSuccess(statement.getStatementId().toString());

        assertEquals(ApplicationStatus.DOCUMENT_CREATED, statement.getStatus());
        verify(statementRepository, times(1)).findById(statement.getStatementId());
        verify(statementRepository, times(1)).save(any(Statement.class));
    }

    @Test
    void handleKafkaCreditSuccessShouldUpdateStatementAndCreditStatus() {
        statement.setCredit(credit);
        when(statementRepository.findById(statement.getStatementId())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);

        dealService.handleKafkaCreditSuccess(statement.getStatementId().toString());

        assertEquals(ApplicationStatus.CREDIT_ISSUED, statement.getStatus());
        assertEquals(CreditStatus.ISSUED, credit.getCreditStatus());
        verify(statementRepository, times(1)).findById(statement.getStatementId());
        verify(statementRepository, times(1)).save(any(Statement.class));
        verify(creditRepository, times(1)).save(any(Credit.class));
    }
}
