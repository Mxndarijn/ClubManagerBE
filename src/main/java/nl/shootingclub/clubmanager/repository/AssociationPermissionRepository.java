package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.AssociationPermission;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssociationPermissionRepository extends JpaRepository<AssociationPermission, UUID> {
    Optional<AssociationPermission> findByName(String name);
}
