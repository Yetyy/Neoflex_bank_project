package neoflex.deal.entity;

import neoflex.deal.entity.StatusHistory;
import neoflex.enums.ChangeType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class StatusHistoryEntityTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        StatusHistory statusHistory1 = StatusHistory.builder()
                .status("APPROVED")
                .time(now)
                .changeType(ChangeType.MANUAL)
                .build();

        StatusHistory statusHistory2 = StatusHistory.builder()
                .status("APPROVED")
                .time(now)
                .changeType(ChangeType.MANUAL)
                .build();

        StatusHistory statusHistory3 = StatusHistory.builder()
                .status("REJECTED")
                .time(now.plusHours(1))
                .changeType(ChangeType.AUTOMATIC)
                .build();

        // Рефлексивность
        assertTrue(statusHistory1.equals(statusHistory1));
        assertEquals(statusHistory1.hashCode(), statusHistory1.hashCode());

        // Симметричность
        assertTrue(statusHistory1.equals(statusHistory2));
        assertTrue(statusHistory2.equals(statusHistory1));
        assertEquals(statusHistory1.hashCode(), statusHistory2.hashCode());

        // Транзитивность
        StatusHistory statusHistory4 = StatusHistory.builder()
                .status("APPROVED")
                .time(now)
                .changeType(ChangeType.MANUAL)
                .build();
        assertTrue(statusHistory1.equals(statusHistory2));
        assertTrue(statusHistory2.equals(statusHistory4));
        assertTrue(statusHistory1.equals(statusHistory4));
        assertEquals(statusHistory1.hashCode(), statusHistory4.hashCode());

        // Консистентность
        assertTrue(statusHistory1.equals(statusHistory2));
        assertTrue(statusHistory1.equals(statusHistory2));

        // Сравнение с null
        assertFalse(statusHistory1 == null);

        // Сравнение с объектом другого класса
        assertFalse(statusHistory1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(statusHistory1, statusHistory3);
        assertNotEquals(statusHistory1.hashCode(), statusHistory3.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        StatusHistory statusHistory = StatusHistory.builder()
                .status("APPROVED")
                .time(now)
                .changeType(ChangeType.MANUAL)
                .build();

        String toString = statusHistory.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("status=APPROVED"));
        assertTrue(toString.contains("time=" + now));
        assertTrue(toString.contains("changeType=MANUAL"));
    }
}
