package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.competition.CompetitionUser;
import nl.shootingclub.clubmanager.model.competition.CompetitionUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompetitionUserRepository extends JpaRepository<CompetitionUser, CompetitionUserId> {

    Optional<CompetitionUser> findByUserIdAndCompetitionId(UUID userId, UUID competitionId);
}
