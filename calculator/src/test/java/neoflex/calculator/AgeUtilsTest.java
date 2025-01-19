package neoflex.calculator;


import neoflex.calculator.util.AgeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgeUtilsTest {

    @Test
    void testCalculateAge_validAge() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        LocalDate currentDate = LocalDate.of(2024, 5, 15);
        int age = AgeUtils.calculateAge(birthDate, currentDate);
        assertEquals(34, age);
    }

    @Test
    void testCalculateAge_currentYear() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        int age = AgeUtils.calculateAge(birthDate, currentDate);
        assertEquals(24, age);
    }

    @Test
    void testCalculateAge_futureDate() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        LocalDate currentDate = LocalDate.of(2025, 1, 1);
        int age = AgeUtils.calculateAge(birthDate, currentDate);
        assertEquals(25, age);
    }

    @Test
    void testCalculateAge_sameDate() {
        LocalDate birthDate = LocalDate.of(2024, 5, 15);
        LocalDate currentDate = LocalDate.of(2024, 5, 15);
        int age = AgeUtils.calculateAge(birthDate, currentDate);
        assertEquals(0, age);
    }
}
