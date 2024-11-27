package neoflex.deal.util;

import neoflex.deal.dto.PaymentScheduleElementDto;
import neoflex.deal.entity.PaymentScheduleElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилита для преобразования DTO в сущности.
 */
public class DtoConverter {

    /**
     * Преобразует список PaymentScheduleElementDto в список PaymentScheduleElement.
     *
     * @param dtos список DTO для преобразования
     * @return список сущностей PaymentScheduleElement
     */
    public static List<PaymentScheduleElement> convertToPaymentScheduleElements(List<PaymentScheduleElementDto> dtos) {
        return dtos.stream()
                .map(dto -> PaymentScheduleElement.builder()
                        .number(dto.getNumber())
                        .date(dto.getDate())
                        .totalPayment(dto.getTotalPayment())
                        .interestPayment(dto.getInterestPayment())
                        .debtPayment(dto.getDebtPayment())
                        .remainingDebt(dto.getRemainingDebt())
                        .build())
                .collect(Collectors.toList());
    }
}
