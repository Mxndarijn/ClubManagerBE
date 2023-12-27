package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.CompetitionUser;
import nl.shootingclub.clubmanager.model.CompetitionUserId;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionUserRepository extends JpaRepository<CompetitionUser, CompetitionUserId> {
}
