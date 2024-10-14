package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AssociationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssociationRoleRepository extends JpaRepository<AssociationRole, UUID> {
    Optional<AssociationRole> findByName(String name);
}
