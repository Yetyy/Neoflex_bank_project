package neoflex.deal.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.deal.entity.Employment;

/**
 * Конвертер для преобразования объектов {@link Employment} в JSON строку и обратно.
 * Используется для хранения объектов {@link Employment} в базе данных в виде JSON.
 */
@Converter(autoApply = true)
public class EmploymentConverter implements AttributeConverter<Employment, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Преобразует объект {@link Employment} в JSON строку для сохранения в базе данных.
     *
     * @param employment объект {@link Employment} для преобразования
     * @return JSON строка, представляющая объект {@link Employment}
     */
    @Override
    public String convertToDatabaseColumn(Employment employment) {
        if (employment == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(employment);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting Employment to JSON", e);
        }
    }

    /**
     * Преобразует JSON строку в объект {@link Employment}.
     *
     * @param dbData JSON строка, представляющая объект {@link Employment}
     * @return объект {@link Employment}, созданный из JSON строки
     */
    @Override
    public Employment convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Employment.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to Employment", e);
        }
    }
}
