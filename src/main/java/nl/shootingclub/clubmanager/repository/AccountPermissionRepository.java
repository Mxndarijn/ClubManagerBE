package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountPermissionRepository extends JpaRepository<AccountPermission, UUID> {
    Optional<AccountPermission> findByNameEquals(String name);
}
