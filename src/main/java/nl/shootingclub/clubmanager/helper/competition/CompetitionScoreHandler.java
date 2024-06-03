package nl.shootingclub.clubmanager.helper.competition;

import nl.shootingclub.clubmanager.model.AssociationCompetition;
import nl.shootingclub.clubmanager.repository.CompetitionUserRepository;

public interface CompetitionScoreHandler {
    void recalculateRanking(AssociationCompetition competition, CompetitionUserRepository repo);
}
