package neoflex.deal.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.deal.entity.PaymentScheduleElement;
import neoflex.dto.LoanOfferDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SerializationUtil {
    private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);

    /**
     * Сериализует кредитное предложение в JSON строку.
     *
     * @param offer кредитное предложение
     * @param objectMapper объект для сериализации
     * @return JSON строка кредитного предложения
     */
    public static String serializeLoanOffer(LoanOfferDto offer, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(offer);
        } catch (Exception e) {
            logger.error("Ошибка сериализации предложения: {}", e.getMessage());
            throw new RuntimeException("Ошибка сериализации предложения", e);
        }
    }

    /**
     * Десериализует кредитное предложение из JSON строки.
     *
     * @param appliedOfferJson JSON строка кредитного предложения
     * @param objectMapper объект для десериализации
     * @return десериализованное кредитное предложение
     */
    public static LoanOfferDto deserializeLoanOffer(String appliedOfferJson, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(appliedOfferJson, LoanOfferDto.class);
        } catch (IOException e) {
            logger.error("Ошибка при десериализации кредитного предложения: {}", e.getMessage());
            throw new RuntimeException("Ошибка при десериализации кредитного предложения", e);
        }
    }

    /**
     * Сериализует график платежей в JSON строку.
     *
     * @param paymentScheduleElements список элементов графика платежей
     * @param objectMapper объект для сериализации
     * @return JSON строка графика платежей
     */
    public static String serializePaymentSchedule(List<PaymentScheduleElement> paymentScheduleElements, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(paymentScheduleElements);
        } catch (Exception e) {
            logger.error("Ошибка сериализации графика платежей: {}", e.getMessage());
            throw new RuntimeException("Ошибка сериализации графика платежей", e);
        }
    }

    /**
     * Десериализует график платежей из JSON строки.
     *
     * @param paymentScheduleJson JSON строка графика платежей
     * @param objectMapper объект для десериализации
     * @return десериализованный список элементов графика платежей
     */
    public static List<PaymentScheduleElement> deserializePaymentSchedule(String paymentScheduleJson, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(paymentScheduleJson, new TypeReference<List<PaymentScheduleElement>>(){});
        } catch (IOException e) {
            logger.error("Ошибка при десериализации графика платежей: {}", e.getMessage());
            throw new RuntimeException("Ошибка при десериализации графика платежей", e);
        }
    }
}
