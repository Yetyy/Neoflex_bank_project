package neoflex.calculator.util;

import java.time.LocalDate;
import java.time.Period;

public class AgeUtils {

    private AgeUtils() {
    }

    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }
}
