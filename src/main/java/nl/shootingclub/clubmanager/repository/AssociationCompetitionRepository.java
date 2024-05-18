package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssociationCompetitionRepository extends JpaRepository<Competition, UUID> {
    List<Competition> findAllByEndDateBeforeAndActiveTrue(LocalDateTime endDate);
}
