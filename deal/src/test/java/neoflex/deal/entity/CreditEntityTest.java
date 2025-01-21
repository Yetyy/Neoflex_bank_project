package neoflex.deal.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CreditEntityTest {

    @Test
    void testEqualsAndHashCode() {
        Credit credit1 = Credit.builder()
                .amount(BigDecimal.TEN)
                .term(12)
                .monthlyPayment(BigDecimal.ONE)
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .paymentSchedule("{\"payments\": []}")
                .insuranceEnabled(true)
                .salaryClient(true)
                .build();

        Credit credit2 = Credit.builder()
                .amount(BigDecimal.TEN)
                .term(12)
                .monthlyPayment(BigDecimal.ONE)
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .paymentSchedule("{\"payments\": []}")
                .insuranceEnabled(true)
                .salaryClient(true)
                .build();

        Credit credit3 = Credit.builder()
                .amount(BigDecimal.valueOf(20))
                .term(24)
                .monthlyPayment(BigDecimal.valueOf(2))
                .rate(BigDecimal.valueOf(0.10))
                .psk(BigDecimal.valueOf(0.02))
                .paymentSchedule("{\"payments\": [1]}")
                .insuranceEnabled(false)
                .salaryClient(false)
                .build();

        // Рефлексивность
        assertTrue(credit1.equals(credit1));
        assertEquals(credit1.hashCode(), credit1.hashCode());

        // Симметричность
        assertTrue(credit1.equals(credit2));
        assertTrue(credit2.equals(credit1));
        assertEquals(credit1.hashCode(), credit2.hashCode());

        // Транзитивность
        Credit credit4 = Credit.builder()
                .amount(BigDecimal.TEN)
                .term(12)
                .monthlyPayment(BigDecimal.ONE)
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .paymentSchedule("{\"payments\": []}")
                .insuranceEnabled(true)
                .salaryClient(true)
                .build();
        assertTrue(credit1.equals(credit2));
        assertTrue(credit2.equals(credit4));
        assertTrue(credit1.equals(credit4));
        assertEquals(credit1.hashCode(), credit4.hashCode());

        // Консистентность
        assertTrue(credit1.equals(credit2));
        assertTrue(credit1.equals(credit2));

        // Сравнение с null
        assertFalse(credit1 == null);

        // Сравнение с объектом другого класса
        assertFalse(credit1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(credit1, credit3);
        assertNotEquals(credit1.hashCode(), credit3.hashCode());
    }

    @Test
    void testToString() {
        Credit credit = Credit.builder()
                .amount(BigDecimal.TEN)
                .term(12)
                .monthlyPayment(BigDecimal.ONE)
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .paymentSchedule("{\"payments\": []}")
                .insuranceEnabled(true)
                .salaryClient(true)
                .build();

        String toString = credit.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("amount=10"));
        assertTrue(toString.contains("term=12"));
        assertTrue(toString.contains("monthlyPayment=1"));
        assertTrue(toString.contains("rate=0.05"));
        assertTrue(toString.contains("psk=0.01"));
        assertTrue(toString.contains("paymentSchedule={\"payments\": []}"));
        assertTrue(toString.contains("insuranceEnabled=true"));
        assertTrue(toString.contains("salaryClient=true"));
    }
}
