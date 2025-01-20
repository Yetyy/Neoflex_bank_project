package neoflex.deal.repository;

import neoflex.deal.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/**
 * Репозиторий для работы с сущностью Statement.
 */
public interface StatementRepository extends JpaRepository<Statement, UUID> {
}
