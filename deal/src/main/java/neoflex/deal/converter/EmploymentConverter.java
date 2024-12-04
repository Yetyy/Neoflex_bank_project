package neoflex.deal.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.deal.entity.Employment;

@Converter(autoApply = true)
public class EmploymentConverter implements AttributeConverter<Employment, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
