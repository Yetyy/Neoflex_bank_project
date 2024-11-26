package neoflex.deal.repository;

import neoflex.deal.entity.PaymentScheduleElement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentScheduleElementRepository extends JpaRepository<PaymentScheduleElement, UUID> {
}
