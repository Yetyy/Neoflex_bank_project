package neoflex.deal.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Утилитарный класс для генерации SES кода.
 */
public class SesCodeGenerator {

    private SesCodeGenerator() {
    }

    /**
     * Генерирует случайный 8-значный SES код.
     *
     * @return сгенерированный SES код
     */
    public static String generateSesCode() {
        return RandomStringUtils.randomNumeric(8);
    }
}
