package neoflex.deal.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.deal.entity.Passport;

@Converter(autoApply = true)
public class PassportConverter implements AttributeConverter<Passport, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Passport passport) {
        if (passport == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(passport);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting Passport to JSON", e);
        }
    }

    @Override
    public Passport convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Passport.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to Passport", e);
        }
    }
}
