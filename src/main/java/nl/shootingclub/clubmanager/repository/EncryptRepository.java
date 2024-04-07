package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.EncryptInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EncryptRepository extends JpaRepository<EncryptInfo, UUID> {

}
