package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.competition.CompetitionScoreTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionScoreTimeRepository extends JpaRepository<CompetitionScoreTime, UUID> {

}
