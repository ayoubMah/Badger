package upec.badge.core_operational_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upec.badge.core_operational_backend.model.RegisteredPerson;

import java.util.UUID;
import java.util.Optional;

public interface RegisteredPersonRepository extends JpaRepository<RegisteredPerson, UUID> {
    Optional<RegisteredPerson> findByBadgeId(String badgeId);
}