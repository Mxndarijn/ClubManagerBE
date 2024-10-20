package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.account.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRoleRepository extends JpaRepository<AccountRole, UUID> {
    Optional<AccountRole> findByName(String name);
}
