package neoflex.deal.repository;

import neoflex.deal.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}
