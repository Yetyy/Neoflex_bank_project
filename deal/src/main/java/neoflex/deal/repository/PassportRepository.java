package neoflex.deal.repository;

import neoflex.deal.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PassportRepository extends JpaRepository<Passport, UUID> {
}
