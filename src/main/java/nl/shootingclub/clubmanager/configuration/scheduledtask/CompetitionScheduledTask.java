package nl.shootingclub.clubmanager.configuration.scheduledtask;

import nl.shootingclub.clubmanager.model.Competition;
import nl.shootingclub.clubmanager.service.AssociationCompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<Competition> competitions = associationCompetitionService.getCompetitionsThatNeedToBeInactive();
        competitions.forEach(competition -> {
            competition.setActive(false);
        });
    }
}
