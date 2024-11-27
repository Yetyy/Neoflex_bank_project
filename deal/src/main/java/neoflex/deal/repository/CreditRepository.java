package neoflex.deal.repository;

import neoflex.deal.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/**
 * Репозиторий для работы с сущностью Credit.
 */
public interface CreditRepository extends JpaRepository<Credit, UUID> {
}
