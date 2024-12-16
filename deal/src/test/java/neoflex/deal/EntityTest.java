package neoflex.deal;

import neoflex.deal.entity.*;
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
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .passport(passport)
                .employment(employment)
                .build();

        when(clientRepository.save(client)).thenReturn(client);

        Client savedClient = clientRepository.save(client);
        assertNotNull(savedClient);
        assertEquals("John", savedClient.getFirstName());
        assertEquals("Doe", savedClient.getLastName());
        assertEquals(Gender.MALE, savedClient.getGender());
        assertEquals(MaritalStatus.SINGLE, savedClient.getMaritalStatus());
        assertEquals("1234", savedClient.getPassport().getSeries());
        assertEquals("567890", savedClient.getPassport().getNumber());
        assertEquals(EmploymentStatus.EMPLOYED, savedClient.getEmployment().getStatus());
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
    }

    @Test
    public void testStatementEntity() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Statement statement = Statement.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(now)
                .signDate(now)
                .sesCode("123456")
                .build();

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
}
