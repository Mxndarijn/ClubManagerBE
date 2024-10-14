package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.competition.AssociationCompetition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AssociationCompetitionRepository extends JpaRepository<AssociationCompetition, UUID> {
    List<AssociationCompetition> findAllByEndDateBeforeAndActiveTrue(LocalDateTime endDate);
}
