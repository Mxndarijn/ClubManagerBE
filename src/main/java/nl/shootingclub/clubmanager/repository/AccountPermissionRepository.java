package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AccountPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountPermissionRepository extends JpaRepository<AccountPermission, UUID> {
    Optional<AccountPermission> findByNameEquals(String name);

    @Query("SELECT r.permissions FROM User u JOIN u.role r WHERE u.email = :email")
    List<AccountPermission> findPermissionsByUserEmail(String email);
}
