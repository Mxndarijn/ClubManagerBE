package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPresenceRepository extends JpaRepository<UserPresence, UUID> {
}
