package neoflex.deal.repository;

import neoflex.deal.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, UUID> {
}
