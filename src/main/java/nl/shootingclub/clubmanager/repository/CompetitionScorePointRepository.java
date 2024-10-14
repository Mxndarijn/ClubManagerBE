package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.competition.CompetitionScorePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionScorePointRepository extends JpaRepository<CompetitionScorePoint, UUID> {

}
