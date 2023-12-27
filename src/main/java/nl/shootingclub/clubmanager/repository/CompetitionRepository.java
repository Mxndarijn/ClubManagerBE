package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Competition;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> {
}
