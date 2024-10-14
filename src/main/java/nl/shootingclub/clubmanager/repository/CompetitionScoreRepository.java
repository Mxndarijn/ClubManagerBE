package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.competition.CompetitionScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionScoreRepository extends JpaRepository<CompetitionScore, UUID> {

}
