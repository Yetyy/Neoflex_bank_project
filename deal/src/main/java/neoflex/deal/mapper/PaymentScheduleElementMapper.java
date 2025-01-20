package neoflex.deal.mapper;

import neoflex.dto.PaymentScheduleElementDto;
import neoflex.deal.entity.PaymentScheduleElement;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentScheduleElementMapper {

    public static PaymentScheduleElement toEntity(PaymentScheduleElementDto dto) {
        return PaymentScheduleElement.builder()
                .number(dto.getNumber())
                .date(dto.getDate())
                .totalPayment(dto.getTotalPayment())
                .interestPayment(dto.getInterestPayment())
                .debtPayment(dto.getDebtPayment())
                .remainingDebt(dto.getRemainingDebt())
                .build();
    }

    public static List<PaymentScheduleElement> toEntities(List<PaymentScheduleElementDto> dtos) {
        return dtos.stream()
                .map(PaymentScheduleElementMapper::toEntity)
                .collect(Collectors.toList());
    }
}
