package neoflex.deal.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import neoflex.deal.entity.StatusHistory;

import java.util.List;

@Converter(autoApply = true)
public class StatusHistoryConverter implements AttributeConverter<List<StatusHistory>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(List<StatusHistory> statusHistory) {
        if (statusHistory == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(statusHistory);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting StatusHistory list to JSON", e);
        }
    }

    @Override
    public List<StatusHistory> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<StatusHistory>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to StatusHistory list", e);
        }
    }
}
