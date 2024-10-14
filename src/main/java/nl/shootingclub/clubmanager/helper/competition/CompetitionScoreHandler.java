package nl.shootingclub.clubmanager.helper.competition;

import nl.shootingclub.clubmanager.model.competition.AssociationCompetition;
import nl.shootingclub.clubmanager.repository.CompetitionUserRepository;

public interface CompetitionScoreHandler {
    void recalculateRanking(AssociationCompetition competition, CompetitionUserRepository repo);
}
