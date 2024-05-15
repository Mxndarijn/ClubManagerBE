package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssociationCompetitionRepository extends JpaRepository<Competition, UUID> {
}
