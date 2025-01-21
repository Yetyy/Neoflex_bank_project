package neoflex.deal.entity;

import neoflex.deal.entity.PaymentScheduleElement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentScheduleElementEntityTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate date = LocalDate.now();

        PaymentScheduleElement element1 = PaymentScheduleElement.builder()
                .number(1)
                .date(date)
                .totalPayment(BigDecimal.valueOf(1000))
                .interestPayment(BigDecimal.valueOf(100))
                .debtPayment(BigDecimal.valueOf(900))
                .remainingDebt(BigDecimal.valueOf(9000))
                .build();

        PaymentScheduleElement element2 = PaymentScheduleElement.builder()
                .number(1)
                .date(date)
                .totalPayment(BigDecimal.valueOf(1000))
                .interestPayment(BigDecimal.valueOf(100))
                .debtPayment(BigDecimal.valueOf(900))
                .remainingDebt(BigDecimal.valueOf(9000))
                .build();

        PaymentScheduleElement element3 = PaymentScheduleElement.builder()
                .number(2)
                .date(date.plusDays(30))
                .totalPayment(BigDecimal.valueOf(1100))
                .interestPayment(BigDecimal.valueOf(110))
                .debtPayment(BigDecimal.valueOf(990))
                .remainingDebt(BigDecimal.valueOf(8000))
                .build();

        // Рефлексивность
        assertTrue(element1.equals(element1));
        assertEquals(element1.hashCode(), element1.hashCode());

        // Симметричность
        assertTrue(element1.equals(element2));
        assertTrue(element2.equals(element1));
        assertEquals(element1.hashCode(), element2.hashCode());

        // Транзитивность
        PaymentScheduleElement element4 = PaymentScheduleElement.builder()
                .number(1)
                .date(date)
                .totalPayment(BigDecimal.valueOf(1000))
                .interestPayment(BigDecimal.valueOf(100))
                .debtPayment(BigDecimal.valueOf(900))
                .remainingDebt(BigDecimal.valueOf(9000))
                .build();
        assertTrue(element1.equals(element2));
        assertTrue(element2.equals(element4));
        assertTrue(element1.equals(element4));
        assertEquals(element1.hashCode(), element4.hashCode());

        // Консистентность
        assertTrue(element1.equals(element2));
        assertTrue(element1.equals(element2));

        // Сравнение с null
        assertFalse(element1 == null);

        // Сравнение с объектом другого класса
        assertFalse(element1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(element1, element3);
        assertNotEquals(element1.hashCode(), element3.hashCode());
    }

    @Test
    void testToString() {
        LocalDate date = LocalDate.now();
        PaymentScheduleElement element = PaymentScheduleElement.builder()
                .number(1)
                .date(date)
                .totalPayment(BigDecimal.valueOf(1000))
                .interestPayment(BigDecimal.valueOf(100))
                .debtPayment(BigDecimal.valueOf(900))
                .remainingDebt(BigDecimal.valueOf(9000))
                .build();

        String toString = element.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("number=1"));
        assertTrue(toString.contains("date=" + date));
        assertTrue(toString.contains("totalPayment=1000"));
        assertTrue(toString.contains("interestPayment=100"));
        assertTrue(toString.contains("debtPayment=900"));
        assertTrue(toString.contains("remainingDebt=9000"));
    }
}
