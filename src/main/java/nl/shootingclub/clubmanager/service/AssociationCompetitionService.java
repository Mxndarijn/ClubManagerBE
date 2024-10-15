
package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.dto.CompetitionDTO;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.competition.*;
import nl.shootingclub.clubmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssociationCompetitionService {

    @Autowired
    private AssociationCompetitionRepository associationCompetitionRepository;
    @Autowired
    private CompetitionUserRepository competitionUserRepository;
    @Autowired
    private CompetitionScoreRepository competitionScoreRepository;
    @Autowired
    private CompetitionScorePointRepository competitionScorePointRepository;
    @Autowired
    private CompetitionScoreTimeRepository competitionScoreTimeRepository;

    public AssociationCompetition createCompetition(CompetitionDTO competitionDTO, Association association) {

        AssociationCompetition competition = new AssociationCompetition();
        competition.setAssociation(association);
        competition.setCompetitionUsers(new HashSet<>());
        competition.setName(competitionDTO.getName());
        competition.setDescription(competitionDTO.getDescription());
        competition.setRanking(competitionDTO.getCompetitionRanking());
        competition.setScoreType(competitionDTO.getCompetitionScoreType());
        competition.setStartDate(competitionDTO.getStartDate());
        competition.setEndDate(competitionDTO.getEndDate());
        LocalDateTime now = LocalDateTime.now();
            competition.setActive(now.isAfter(competitionDTO.getStartDate()) && now.isBefore(competitionDTO.getEndDate()));

        return associationCompetitionRepository.save(competition);
    }


    public boolean removeUser(AssociationCompetition competition, User user) {
        Optional<CompetitionUser> compUser = competitionUserRepository.findByUserIdAndCompetitionId(user.getId(), competition.getId());
        if(compUser.isPresent()) {
            competitionUserRepository.delete(compUser.get());
            return true;
        }
        return false;
    }
    public CompetitionUser addUser(AssociationCompetition competition, User user) {
        Optional<CompetitionUser> compUser = competitionUserRepository.findByUserIdAndCompetitionId(user.getId(), competition.getId());
        if(compUser.isPresent()) {
            return compUser.get();
        }
        CompetitionUser competitionUser = CompetitionUser.builder()
                .user(user)
                .competition(competition)
                .id(CompetitionUserId.builder().competitionId(competition.getId()).userId(user.getId()).build())
                .build();


        competitionUserRepository.save(competitionUser);

        competitionUser.getCompetition().recalculateRanking(competitionUserRepository);
        return competitionUser;
    }

    public boolean removeUserScore(CompetitionUser competitionUser, UUID competitionScoreId) {
        List<CompetitionScore> list = competitionUser.getScores().stream().filter(c -> {
            return c.getId().equals(competitionScoreId);
        }).toList();
        if (deleteScores(list)) return false;

        competitionScoreRepository.deleteAll(list);
        return true;
    }

    private boolean deleteScores(List<CompetitionScore> list) {
        if(list.isEmpty())
            return true;
        if (list.get(0) instanceof CompetitionScoreTime) {
            List<CompetitionScoreTime> timeScores = list.stream()
                    .map(score -> (CompetitionScoreTime) score)
                    .collect(Collectors.toList());
            competitionScoreTimeRepository.deleteAll(timeScores);
        } else if (list.get(0) instanceof CompetitionScorePoint) {
            List<CompetitionScorePoint> pointScores = list.stream()
                    .map(score -> (CompetitionScorePoint) score)
                    .collect(Collectors.toList());
            competitionScorePointRepository.deleteAll(pointScores);
        }
        return false;
    }

    public boolean removeUserScores(CompetitionUser competitionUser, List<UUID> ids) {
        List<CompetitionScore> list = competitionUser.getScores().stream()
                .filter(c -> ids.contains(c.getId()))
                .toList();
        return !deleteScores(list);
    }

    public CompetitionUser addUserScore(CompetitionUser competitionUser, long score, LocalDate date) {
        CompetitionScore competitionScore = switch (competitionUser.getCompetition().getScoreType()) {
            case TIME -> CompetitionScoreTime.builder().score(Duration.ofNanos(score)).build();
            case POINT -> CompetitionScorePoint.builder().score((int) score).build();
        };

        competitionScore.setCompetitionUser(competitionUser);
        competitionScore.setScoreDate(date);

        competitionScoreRepository.save(competitionScore);
        competitionUser.getScores().add(competitionScore);

        competitionUser.getCompetition().recalculateRanking(competitionUserRepository);
        return competitionUser;
    }

    public Optional<AssociationCompetition> getCompetitionById(UUID competitionID) {
        return associationCompetitionRepository.findById(competitionID);
    }

    public Optional<CompetitionUser> getCompetitionUser(UUID competitionID, UUID userID) {
        return competitionUserRepository.findByUserIdAndCompetitionId(userID, competitionID);
    }

    public List<AssociationCompetition> getCompetitionsThatNeedToBeInactive() {
        return associationCompetitionRepository.findAllByEndDateBeforeAndActiveTrue(LocalDateTime.now());
    }

    public void deleteCompetition(AssociationCompetition competition) {
        associationCompetitionRepository.delete(competition);
    }

    public void saveCompetition(AssociationCompetition competition) {
        associationCompetitionRepository.save(competition);
    }
}
