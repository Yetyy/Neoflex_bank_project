package neoflex.deal.entity;

import neoflex.enums.*;
import neoflex.deal.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntityTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @Test
    public void testClientEntity() {
        Passport passport = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(LocalDate.now())
                .build();
        Employment employment = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();

        Client client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(2)
                .passport(passport)
                .employment(employment)
                .accountNumber("1234567890")
                .build();

        when(clientRepository.save(client)).thenReturn(client);

        Client savedClient = clientRepository.save(client);
        assertNotNull(savedClient);
        assertEquals("John", savedClient.getFirstName());
        assertEquals("Doe", savedClient.getLastName());
        assertEquals("Middle", savedClient.getMiddleName());
        assertEquals(LocalDate.of(1990, 1, 1), savedClient.getBirthDate());
        assertEquals("test@example.com", savedClient.getEmail());
        assertEquals(Gender.MALE, savedClient.getGender());
        assertEquals(MaritalStatus.SINGLE, savedClient.getMaritalStatus());
        assertEquals(2, savedClient.getDependentAmount());
        assertEquals("1234", savedClient.getPassport().getSeries());
        assertEquals("567890", savedClient.getPassport().getNumber());
        assertEquals(EmploymentStatus.EMPLOYED, savedClient.getEmployment().getStatus());
        assertEquals("1234567890", savedClient.getAccountNumber());
    }

    @Test
    public void testClientEntity_NullValues() {
        Client client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        when(clientRepository.save(client)).thenReturn(client);

        Client savedClient = clientRepository.save(client);
        assertNotNull(savedClient);
        assertEquals("John", savedClient.getFirstName());
        assertEquals("Doe", savedClient.getLastName());
        assertNull(savedClient.getMiddleName());
        assertNull(savedClient.getBirthDate());
        assertNull(savedClient.getEmail());
        assertNull(savedClient.getGender());
        assertNull(savedClient.getMaritalStatus());
        assertEquals(0, savedClient.getDependentAmount());
        assertNull(savedClient.getPassport());
        assertNull(savedClient.getEmployment());
        assertNull(savedClient.getAccountNumber());
    }

    @Test
    public void testCreditEntity() {
        Credit credit = Credit.builder()
                .amount(BigDecimal.TEN)
                .term(12)
                .monthlyPayment(BigDecimal.ONE)
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .creditStatus(CreditStatus.CALCULATED)
                .paymentSchedule("{\"payments\": []}")
                .insuranceEnabled(true)
                .salaryClient(true)
                .build();

        when(creditRepository.save(credit)).thenReturn(credit);

        Credit savedCredit = creditRepository.save(credit);
        assertNotNull(savedCredit);
        assertEquals(BigDecimal.TEN, savedCredit.getAmount());
        assertEquals(12, savedCredit.getTerm());
        assertEquals(BigDecimal.ONE, savedCredit.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(0.05), savedCredit.getRate());
        assertEquals(BigDecimal.valueOf(0.01), savedCredit.getPsk());
        assertEquals(CreditStatus.CALCULATED, savedCredit.getCreditStatus());
        assertEquals("{\"payments\": []}", savedCredit.getPaymentSchedule());
        assertTrue(savedCredit.isInsuranceEnabled());
        assertTrue(savedCredit.isSalaryClient());
    }

    @Test
    public void testCreditEntity_NullValues() {
        Credit credit = Credit.builder()
                .amount(BigDecimal.TEN)
                .term(12)
                .build();

        when(creditRepository.save(credit)).thenReturn(credit);

        Credit savedCredit = creditRepository.save(credit);
        assertNotNull(savedCredit);
        assertEquals(BigDecimal.TEN, savedCredit.getAmount());
        assertEquals(12, savedCredit.getTerm());
        assertNull(savedCredit.getMonthlyPayment());
        assertNull(savedCredit.getRate());
        assertNull(savedCredit.getPsk());
        assertNull(savedCredit.getCreditStatus());
        assertNull(savedCredit.getPaymentSchedule());
        assertFalse(savedCredit.isInsuranceEnabled());
        assertFalse(savedCredit.isSalaryClient());
    }

    @Test
    public void testStatementEntity() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Client client = Client.builder().firstName("John").lastName("Doe").build();
        Credit credit = Credit.builder().amount(BigDecimal.TEN).term(12).build();
        List<StatusHistory> statusHistories = List.of(StatusHistory.builder().status("APPROVED").time(now).changeType(ChangeType.MANUAL).build());
        Statement statement = Statement.builder()
                .client(client)
                .credit(credit)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(now)
                .signDate(now)
                .sesCode("123456")
                .appliedOffer("{\"offer\": {}}")
                .statusHistory(statusHistories)
                .build();

        when(statementRepository.save(statement)).thenReturn(statement);

        Statement savedStatement = statementRepository.save(statement);
        assertNotNull(savedStatement);
        assertEquals(client, savedStatement.getClient());
        assertEquals(credit, savedStatement.getCredit());
        assertEquals(ApplicationStatus.PREAPPROVAL, savedStatement.getStatus());
        assertEquals(now, savedStatement.getCreationDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(now, savedStatement.getSignDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals("123456", savedStatement.getSesCode());
        assertEquals("{\"offer\": {}}", savedStatement.getAppliedOffer());
        assertEquals(1, savedStatement.getStatusHistory().size());
        assertEquals("APPROVED", savedStatement.getStatusHistory().get(0).getStatus());
    }

    @Test
    public void testStatementEntity_NullValues() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Statement statement = Statement.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(now)
                .build();

        when(statementRepository.save(statement)).thenReturn(statement);

        Statement savedStatement = statementRepository.save(statement);
        assertNotNull(savedStatement);
        assertNull(savedStatement.getClient());
        assertNull(savedStatement.getCredit());
        assertEquals(ApplicationStatus.PREAPPROVAL, savedStatement.getStatus());
        assertEquals(now, savedStatement.getCreationDate().truncatedTo(ChronoUnit.SECONDS));
        assertNull(savedStatement.getSignDate());
        assertNull(savedStatement.getSesCode());
        assertNull(savedStatement.getAppliedOffer());
        assertNull(savedStatement.getStatusHistory());
    }

    @Test
    public void testStatusHistoryEntity() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        StatusHistory statusHistory = StatusHistory.builder()
                .status("APPROVED")
                .time(now)
                .changeType(ChangeType.MANUAL)
                .build();

        when(statusHistoryRepository.save(statusHistory)).thenReturn(statusHistory);

        StatusHistory savedStatusHistory = statusHistoryRepository.save(statusHistory);
        assertNotNull(savedStatusHistory);
        assertEquals("APPROVED", savedStatusHistory.getStatus());
        assertEquals(now, savedStatusHistory.getTime().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(ChangeType.MANUAL, savedStatusHistory.getChangeType());
    }

    @Test
    public void testStatusHistoryEntity_NullValues() {
        StatusHistory statusHistory = StatusHistory.builder()
                .status("APPROVED")
                .build();

        when(statusHistoryRepository.save(statusHistory)).thenReturn(statusHistory);

        StatusHistory savedStatusHistory = statusHistoryRepository.save(statusHistory);
        assertNotNull(savedStatusHistory);
        assertEquals("APPROVED", savedStatusHistory.getStatus());
        assertNull(savedStatusHistory.getTime());
        assertNull(savedStatusHistory.getChangeType());
    }

    @Test
    public void testPassportEntity() {
        LocalDate issueDate = LocalDate.now();
        Passport passport = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(issueDate)
                .build();

        assertNotNull(passport);
        assertEquals("1234", passport.getSeries());
        assertEquals("567890", passport.getNumber());
        assertEquals("Branch1", passport.getIssueBranch());
        assertEquals(issueDate, passport.getIssueDate());
    }

    @Test
    public void testPassportEntity_NullValues() {
        Passport passport = Passport.builder().build();

        assertNotNull(passport);
        assertNull(passport.getSeries());
        assertNull(passport.getNumber());
        assertNull(passport.getIssueBranch());
        assertNull(passport.getIssueDate());
    }

    @Test
    public void testEmploymentEntity() {
        Employment employment = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();

        assertNotNull(employment);
        assertEquals(EmploymentStatus.EMPLOYED, employment.getStatus());
        assertEquals("1234567890", employment.getEmployerInn());
        assertEquals(BigDecimal.valueOf(50000), employment.getSalary());
        assertEquals(EmploymentPosition.WORKER, employment.getPosition());
        assertEquals(5, employment.getWorkExperienceTotal());
        assertEquals(3, employment.getWorkExperienceCurrent());
    }

    @Test
    public void testEmploymentEntity_NullValues() {
        Employment employment = Employment.builder().build();

        assertNotNull(employment);
        assertNull(employment.getStatus());
        assertNull(employment.getEmployerInn());
        assertNull(employment.getSalary());
        assertNull(employment.getPosition());
        assertNull(employment.getWorkExperienceTotal());
        assertNull(employment.getWorkExperienceCurrent());
    }


}
