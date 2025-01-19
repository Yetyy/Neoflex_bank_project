package neoflex.deal.entity;

import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ClientEntityTest {

    @Test
    void testEqualsAndHashCode() {
        Passport passport1 = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(LocalDate.now())
                .build();
        Employment employment1 = Employment.builder()
                .status(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(3)
                .build();

        Client client1 = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(2)
                .passport(passport1)
                .employment(employment1)
                .accountNumber("1234567890")
                .build();

        Client client2 = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(2)
                .passport(passport1)
                .employment(employment1)
                .accountNumber("1234567890")
                .build();

        Client client3 = Client.builder()
                .firstName("Jane")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .gender(Gender.FEMALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(3)
                .passport(passport1)
                .employment(employment1)
                .accountNumber("1234567891")
                .build();

        // Рефлексивность
        assertTrue(client1.equals(client1));
        assertEquals(client1.hashCode(), client1.hashCode());

        // Симметричность
        assertTrue(client1.equals(client2));
        assertTrue(client2.equals(client1));
        assertEquals(client1.hashCode(), client2.hashCode());

        // Транзитивность
        Client client4 = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(2)
                .passport(passport1)
                .employment(employment1)
                .accountNumber("1234567890")
                .build();
        assertTrue(client1.equals(client2));
        assertTrue(client2.equals(client4));
        assertTrue(client1.equals(client4));
        assertEquals(client1.hashCode(), client4.hashCode());

        // Консистентность
        assertTrue(client1.equals(client2));
        assertTrue(client1.equals(client2));

        // Сравнение с null
        assertFalse(client1.equals(null));

        // Сравнение с объектом другого класса
        assertFalse(client1.equals(new Object()));

        // Сравнение с разными объектами
        assertNotEquals(client1, client3);
        assertNotEquals(client1.hashCode(), client3.hashCode());
    }

    @Test
    void testToString() {
        Passport passport1 = Passport.builder()
                .series("1234")
                .number("567890")
                .issueBranch("Branch1")
                .issueDate(LocalDate.now())
                .build();
        Employment employment1 = Employment.builder()
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
                .passport(passport1)
                .employment(employment1)
                .accountNumber("1234567890")
                .build();

        String toString = client.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("firstName=John"));
        assertTrue(toString.contains("lastName=Doe"));
        assertTrue(toString.contains("middleName=Middle"));
        assertTrue(toString.contains("birthDate=1990-01-01"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("gender=MALE"));
        assertTrue(toString.contains("maritalStatus=SINGLE"));
        assertTrue(toString.contains("dependentAmount=2"));
        assertTrue(toString.contains("accountNumber=1234567890"));
    }
}
