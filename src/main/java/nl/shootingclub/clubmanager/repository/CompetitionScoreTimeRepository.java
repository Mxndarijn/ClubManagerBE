package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.CompetitionScore;
import nl.shootingclub.clubmanager.model.CompetitionScoreTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionScoreTimeRepository extends JpaRepository<CompetitionScoreTime, UUID> {

}
