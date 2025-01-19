package neoflex.deal.entity;

import neoflex.deal.entity.Passport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PassportEntityTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate issueDate = LocalDate.now();

        Passport passport1 = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(issueDate)
                .build();

        Passport passport2 = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(issueDate)
                .build();

        Passport passport3 = Passport.builder()
                .series("5678")
                .number("901234")
                .issueBranch("Branch2")
                .issueDate(issueDate.plusDays(1))
                .build();

        // Рефлексивность
        assertTrue(passport1.equals(passport1));
        assertEquals(passport1.hashCode(), passport1.hashCode());

        // Симметричность
        assertTrue(passport1.equals(passport2));
        assertTrue(passport2.equals(passport1));
        assertEquals(passport1.hashCode(), passport2.hashCode());

        // Транзитивность
        Passport passport4 = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(issueDate)
                .build();
        assertTrue(passport1.equals(passport2));
        assertTrue(passport2.equals(passport4));
        assertTrue(passport1.equals(passport4));
        assertEquals(passport1.hashCode(), passport4.hashCode());

        // Консистентность
        assertTrue(passport1.equals(passport2));
        assertTrue(passport1.equals(passport2));

        // Сравнение с null
        assertFalse(passport1.equals(null));

        // Сравнение с объектом другого класса
        assertFalse(passport1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(passport1, passport3);
        assertNotEquals(passport1.hashCode(), passport3.hashCode());
    }

    @Test
    void testToString() {
        LocalDate issueDate = LocalDate.now();
        Passport passport = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(issueDate)
                .build();

        String toString = passport.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("series=1234"));
        assertTrue(toString.contains("number=567890"));
        assertTrue(toString.contains("issueBranch=Branch1"));
        assertTrue(toString.contains("issueDate=" + issueDate));
    }
}
