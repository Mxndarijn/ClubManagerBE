package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.CompetitionScore;
import nl.shootingclub.clubmanager.model.CompetitionUser;
import nl.shootingclub.clubmanager.model.CompetitionUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompetitionScoreRepository extends JpaRepository<CompetitionScore, UUID> {

}
