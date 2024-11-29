package neoflex.deal.mapper;

import neoflex.deal.dto.PaymentScheduleElementDto;
import neoflex.deal.entity.PaymentScheduleElement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PaymentScheduleElementMapper {
    PaymentScheduleElementMapper INSTANCE = Mappers.getMapper(PaymentScheduleElementMapper.class);

    PaymentScheduleElement toEntity(PaymentScheduleElementDto dto);
    List<PaymentScheduleElement> toEntities(List<PaymentScheduleElementDto> dtos);
}
