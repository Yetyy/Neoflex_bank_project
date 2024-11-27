package neoflex.deal.repository;

import neoflex.deal.entity.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/**
 * Репозиторий для работы с сущностью Employment.
 */
public interface EmploymentRepository extends JpaRepository<Employment, UUID> {
}
