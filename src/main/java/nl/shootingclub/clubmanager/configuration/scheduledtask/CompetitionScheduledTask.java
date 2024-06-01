package nl.shootingclub.clubmanager.configuration.scheduledtask;

import nl.shootingclub.clubmanager.model.AssociationCompetition;
import nl.shootingclub.clubmanager.service.AssociationCompetitionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompetitionScheduledTask {
    private final AssociationCompetitionService associationCompetitionService;

    public CompetitionScheduledTask(AssociationCompetitionService associationCompetitionService) {
        this.associationCompetitionService = associationCompetitionService;
    }

    @Scheduled(cron = "0 0 */6 * * ?")  // Deze cron expression betekent: elke 6 uur uitvoeren
    public void reportCurrentTime() {
        List<AssociationCompetition> competitions = associationCompetitionService.getCompetitionsThatNeedToBeInactive();
        competitions.forEach(competition -> {
            competition.setActive(false);
        });
    }
}
