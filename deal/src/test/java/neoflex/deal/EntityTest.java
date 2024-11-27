package neoflex.deal;

import neoflex.deal.entity.*;
import neoflex.deal.enums.*;
import neoflex.deal.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntityTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private EmploymentRepository employmentRepository;

    @Mock
    private PassportRepository passportRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @Test
    public void testClientEntity() {
        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setGender(Gender.MALE);
        client.setMaritalStatus(MaritalStatus.SINGLE);

        when(clientRepository.save(client)).thenReturn(client);

        Client savedClient = clientRepository.save(client);
        assertNotNull(savedClient);
        assertEquals("John", savedClient.getFirstName());
        assertEquals("Doe", savedClient.getLastName());
        assertEquals(Gender.MALE, savedClient.getGender());
        assertEquals(MaritalStatus.SINGLE, savedClient.getMaritalStatus());
    }

    @Test
    public void testCreditEntity() {
        Credit credit = new Credit();
        credit.setAmount(BigDecimal.TEN);
        credit.setTerm(12);
        credit.setMonthlyPayment(BigDecimal.ONE);
        credit.setRate(BigDecimal.valueOf(0.05));
        credit.setPsk(BigDecimal.valueOf(0.01));
        credit.setCreditStatus(CreditStatus.CALCULATED);

        when(creditRepository.save(credit)).thenReturn(credit);

        Credit savedCredit = creditRepository.save(credit);
        assertNotNull(savedCredit);
        assertEquals(BigDecimal.TEN, savedCredit.getAmount());
        assertEquals(12, savedCredit.getTerm());
        assertEquals(BigDecimal.ONE, savedCredit.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(0.05), savedCredit.getRate());
        assertEquals(BigDecimal.valueOf(0.01), savedCredit.getPsk());
        assertEquals(CreditStatus.CALCULATED, savedCredit.getCreditStatus());
    }

    @Test
    public void testEmploymentEntity() {
        Employment employment = new Employment();
        employment.setStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerInn("1234567890");
        employment.setSalary(BigDecimal.valueOf(50000));
        employment.setPosition(EmploymentPosition.WORKER);
        employment.setWorkExperienceTotal(5);
        employment.setWorkExperienceCurrent(3);

        when(employmentRepository.save(employment)).thenReturn(employment);

        Employment savedEmployment = employmentRepository.save(employment);
        assertNotNull(savedEmployment);
        assertEquals(EmploymentStatus.EMPLOYED, savedEmployment.getStatus());
        assertEquals("1234567890", savedEmployment.getEmployerInn());
        assertEquals(BigDecimal.valueOf(50000), savedEmployment.getSalary());
        assertEquals(EmploymentPosition.WORKER, savedEmployment.getPosition());
        assertEquals(5, savedEmployment.getWorkExperienceTotal());
        assertEquals(3, savedEmployment.getWorkExperienceCurrent());
    }

    @Test
    public void testPassportEntity() {
        Passport passport = new Passport();
        passport.setSeries("AB");
        passport.setNumber("123456");
        passport.setIssueBranch("Branch1");
        passport.setIssueDate(LocalDate.now());

        when(passportRepository.save(passport)).thenReturn(passport);

        Passport savedPassport = passportRepository.save(passport);
        assertNotNull(savedPassport);
        assertEquals("AB", savedPassport.getSeries());
        assertEquals("123456", savedPassport.getNumber());
        assertEquals("Branch1", savedPassport.getIssueBranch());
        assertEquals(LocalDate.now(), savedPassport.getIssueDate());
    }

    @Test
    public void testStatementEntity() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Statement statement = new Statement();
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        statement.setCreationDate(now);
        statement.setSignDate(now);
        statement.setSesCode("123456");

        when(statementRepository.save(statement)).thenReturn(statement);

        Statement savedStatement = statementRepository.save(statement);
        assertNotNull(savedStatement);
        assertEquals(ApplicationStatus.PREAPPROVAL, savedStatement.getStatus());
        assertEquals(now, savedStatement.getCreationDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(now, savedStatement.getSignDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals("123456", savedStatement.getSesCode());
    }

    @Test
    public void testStatusHistoryEntity() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setStatus("APPROVED");
        statusHistory.setTime(now);
        statusHistory.setChangeType(ChangeType.MANUAL);

        when(statusHistoryRepository.save(statusHistory)).thenReturn(statusHistory);

        StatusHistory savedStatusHistory = statusHistoryRepository.save(statusHistory);
        assertNotNull(savedStatusHistory);
        assertEquals("APPROVED", savedStatusHistory.getStatus());
        assertEquals(now, savedStatusHistory.getTime().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(ChangeType.MANUAL, savedStatusHistory.getChangeType());
    }
}
