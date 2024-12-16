//package neoflex.deal.converter;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import neoflex.deal.entity.Passport;
//
///**
// * Конвертер для преобразования объектов {@link Passport} в JSON строку и обратно.
// * Используется для хранения объектов {@link Passport} в базе данных в виде JSON.
// */
//@Converter(autoApply = true)
//public class PassportConverter implements AttributeConverter<Passport, String> {
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * Преобразует объект {@link Passport} в JSON строку для сохранения в базе данных.
//     *
//     * @param passport объект {@link Passport} для преобразования
//     * @return JSON строка, представляющая объект {@link Passport}
//     */
//    @Override
//    public String convertToDatabaseColumn(Passport passport) {
//        if (passport == null) {
//            return null;
//        }
//        try {
//            return objectMapper.writeValueAsString(passport);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Error converting Passport to JSON", e);
//        }
//    }
//
//    /**
//     * Преобразует JSON строку в объект {@link Passport}.
//     *
//     * @param dbData JSON строка, представляющая объект {@link Passport}
//     * @return объект {@link Passport}, созданный из JSON строки
//     */
//    @Override
//    public Passport convertToEntityAttribute(String dbData) {
//        if (dbData == null) {
//            return null;
//        }
//        try {
//            return objectMapper.readValue(dbData, Passport.class);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Error converting JSON to Passport", e);
//        }
//    }
//}
