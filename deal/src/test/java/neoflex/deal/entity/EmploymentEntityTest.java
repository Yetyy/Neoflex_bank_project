package neoflex.deal.entity;

import neoflex.deal.entity.Employment;
import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class EmploymentEntityTest {

    @Test
    void testEqualsAndHashCode() {
        Employment employment1 = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();

        Employment employment2 = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();

        Employment employment3 = Employment.builder()
                .status(EmploymentStatus.SELF_EMPLOYED)
                .employerInn("0987654321")
                .salary(BigDecimal.valueOf(60000))
                .position(EmploymentPosition.MIDDLE_MANAGER)
                .workExperienceTotal(10)
                .workExperienceCurrent(7)
                .build();

        // Рефлексивность
        assertTrue(employment1.equals(employment1));
        assertEquals(employment1.hashCode(), employment1.hashCode());

        // Симметричность
        assertTrue(employment1.equals(employment2));
        assertTrue(employment2.equals(employment1));
        assertEquals(employment1.hashCode(), employment2.hashCode());

        // Транзитивность
        Employment employment4 = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();
        assertTrue(employment1.equals(employment2));
        assertTrue(employment2.equals(employment4));
        assertTrue(employment1.equals(employment4));
        assertEquals(employment1.hashCode(), employment4.hashCode());

        // Консистентность
        assertTrue(employment1.equals(employment2));
        assertTrue(employment1.equals(employment2));

        // Сравнение с null
        assertFalse(employment1.equals(null));

        // Сравнение с объектом другого класса
        assertFalse(employment1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(employment1, employment3);
        assertNotEquals(employment1.hashCode(), employment3.hashCode());
    }

    @Test
    void testToString() {
        Employment employment = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();

        String toString = employment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("status=EMPLOYED"));
        assertTrue(toString.contains("employerInn=1234567890"));
        assertTrue(toString.contains("salary=50000"));
        assertTrue(toString.contains("position=WORKER"));
        assertTrue(toString.contains("workExperienceTotal=5"));
        assertTrue(toString.contains("workExperienceCurrent=3"));
    }
}
