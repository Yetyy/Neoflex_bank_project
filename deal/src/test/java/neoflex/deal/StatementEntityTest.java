package neoflex.deal;

import neoflex.deal.entity.Statement;
import neoflex.deal.entity.Client;
import neoflex.deal.entity.Credit;
import neoflex.deal.entity.StatusHistory;
import neoflex.enums.ApplicationStatus;
import neoflex.enums.ChangeType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatementEntityTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Client client1 = Client.builder().firstName("John").lastName("Doe").build();
        Credit credit1 = Credit.builder().amount(BigDecimal.TEN).term(12).build();
        List<StatusHistory> statusHistories1 = List.of(StatusHistory.builder().status("APPROVED").time(now).changeType(ChangeType.MANUAL).build());

        Statement statement1 = Statement.builder()
                .client(client1)
                .credit(credit1)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(now)
                .signDate(now)
                .sesCode("123456")
                .appliedOffer("{\"offer\": {}}")
                .statusHistory(statusHistories1)
                .build();

        Statement statement2 = Statement.builder()
                .client(client1)
                .credit(credit1)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(now)
                .signDate(now)
                .sesCode("123456")
                .appliedOffer("{\"offer\": {}}")
                .statusHistory(statusHistories1)
                .build();

        Client client2 = Client.builder().firstName("Jane").lastName("Doe").build();
        Credit credit2 = Credit.builder().amount(BigDecimal.valueOf(20)).term(24).build();
        List<StatusHistory> statusHistories2 = List.of(StatusHistory.builder().status("REJECTED").time(now).changeType(ChangeType.AUTOMATIC).build());

        Statement statement3 = Statement.builder()
                .client(client2)
                .credit(credit2)
                .status(ApplicationStatus.APPROVED)
                .creationDate(now.plusDays(1))
                .signDate(now.plusDays(1))
                .sesCode("654321")
                .appliedOffer("{\"offer\": [1]}")
                .statusHistory(statusHistories2)
                .build();

        // Рефлексивность
        assertTrue(statement1.equals(statement1));
        assertEquals(statement1.hashCode(), statement1.hashCode());

        // Симметричность
        assertTrue(statement1.equals(statement2));
        assertTrue(statement2.equals(statement1));
        assertEquals(statement1.hashCode(), statement2.hashCode());

        // Транзитивность
        Statement statement4 = Statement.builder()
                .client(client1)
                .credit(credit1)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(now)
                .signDate(now)
                .sesCode("123456")
                .appliedOffer("{\"offer\": {}}")
                .statusHistory(statusHistories1)
                .build();
        assertTrue(statement1.equals(statement2));
        assertTrue(statement2.equals(statement4));
        assertTrue(statement1.equals(statement4));
        assertEquals(statement1.hashCode(), statement4.hashCode());

        // Консистентность
        assertTrue(statement1.equals(statement2));
        assertTrue(statement1.equals(statement2));

        // Сравнение с null
        assertFalse(statement1.equals(null));

        // Сравнение с объектом другого класса
        assertFalse(statement1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(statement1, statement3);
        assertNotEquals(statement1.hashCode(), statement3.hashCode());
    }

    @Test
    void testToString() {
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

        String toString = statement.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("client=" + client));
        assertTrue(toString.contains("credit=" + credit));
        assertTrue(toString.contains("status=PREAPPROVAL"));
        assertTrue(toString.contains("creationDate=" + now));
        assertTrue(toString.contains("signDate=" + now));
        assertTrue(toString.contains("sesCode=123456"));
        assertTrue(toString.contains("appliedOffer={\"offer\": {}}"));
        assertTrue(toString.contains("statusHistory=" + statusHistories));
    }
}
