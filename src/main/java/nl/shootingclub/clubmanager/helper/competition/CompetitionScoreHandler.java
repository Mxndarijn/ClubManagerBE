package nl.shootingclub.clubmanager.helper.competition;

import nl.shootingclub.clubmanager.model.AssociationCompetition;

public interface CompetitionScoreHandler {


    void recalculateRanking(AssociationCompetition competition);
}
