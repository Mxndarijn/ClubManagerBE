
package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.dto.CompetitionDTO;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AssociationCompetitionRepository;
import nl.shootingclub.clubmanager.repository.CompetitionUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AssociationCompetitionService {

    @Autowired
    private AssociationCompetitionRepository associationCompetitionRepository;
    @Autowired
    private CompetitionUserRepository competitionUserRepository;

    public Competition createCompetition(CompetitionDTO competitionDTO, Association association) {

        Competition competition = new Competition();
        competition.setAssociation(association);
        competition.setCompetitionUsers(new HashSet<>());
        competition.setName(competitionDTO.getName());
        competition.setDescription(competitionDTO.getDescription());
        competition.setRanking(competitionDTO.getCompetitionRanking());
        competition.setScoreType(competitionDTO.getCompetitionScoreType());
        competition.setStartDate(competitionDTO.getStartDate());
        competition.setEndDate(competitionDTO.getEndDate());

        return associationCompetitionRepository.save(competition);
    }


    public boolean removeUser(Competition competition, User user) {
        Optional<CompetitionUser> compUser = competitionUserRepository.findByUserIdAndCompetitionId(user.getId(), competition.getId());
        if(compUser.isPresent()) {
            competitionUserRepository.delete(compUser.get());
            return true;
        }
        return false;
    }
    public CompetitionUser addUser(Competition competition, User user) {
        Optional<CompetitionUser> compUser = competitionUserRepository.findByUserIdAndCompetitionId(user.getId(), competition.getId());
        if(compUser.isPresent()) {
            return compUser.get();
        }
        CompetitionUser competitionUser = CompetitionUser.builder()
                .user(user)
                .competition(competition)
                .id(CompetitionUserId.builder().competitionId(competition.getId()).userId(user.getId()).build())
                .build();

        competition.getCompetitionUsers().add(competitionUser);

        competitionUser.getCompetition().recalculateRanking();
        return competitionUser;
    }

    public boolean removeUserScore(CompetitionUser competitionUser, UUID competitionScoreId) {
        List<CompetitionScore> list = competitionUser.getScores().stream().filter(c -> {
            return c.getId().equals(competitionScoreId);
        }).toList();
        if(list.isEmpty())
            return false;
        list.forEach(competitionUser.getScores()::remove);

        return true;
    }

    public CompetitionUser addUserScore(CompetitionUser competitionUser, long score, LocalDateTime date) {
        CompetitionScore competitionScore = switch (competitionUser.getCompetition().getScoreType()) {
            case TIME -> CompetitionScoreTime.builder().score(Duration.ofNanos(score)).build();
            case POINT -> CompetitionScorePoint.builder().score((int) score).build();
        };

        competitionScore.setCompetitionUser(competitionUser);
        competitionScore.setScoreDate(date);

        competitionUser.getScores().add(competitionScore);

        competitionUser.getCompetition().recalculateRanking();
        return competitionUser;
    }

    public Optional<Competition> getCompetitionById(UUID competitionID) {
        return associationCompetitionRepository.findById(competitionID);
    }

    public Optional<CompetitionUser> getCompetitionUser(UUID competitionID, UUID userID) {
        return competitionUserRepository.findByUserIdAndCompetitionId(userID, competitionID);
    }

    public List<Competition> getCompetitionsThatNeedToBeInactive() {
        return associationCompetitionRepository.findAllByEndDateBeforeAndActiveTrue(LocalDateTime.now());
    }

    public void deleteCompetition(Competition competition) {
        associationCompetitionRepository.delete(competition);
    }

    public void saveCompetition(Competition competition) {
        associationCompetitionRepository.save(competition);
    }
}