package nl.shootingclub.clubmanager.helper.competition;

import nl.shootingclub.clubmanager.configuration.data.CompetitionRanking;
import nl.shootingclub.clubmanager.model.AssociationCompetition;
import nl.shootingclub.clubmanager.model.CompetitionUser;
import nl.shootingclub.clubmanager.repository.CompetitionUserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompetitionScoreHandlerPoint implements CompetitionScoreHandler {
    @Override
    public void recalculateRanking(AssociationCompetition competition, CompetitionUserRepository repo) {
        CompetitionRanking ranking = competition.getRanking();

        Map<CompetitionUser, List<Long>> map = competition.getCompetitionUsers().stream()
                .collect(Collectors.toMap(
                        user -> user,  // key is de CompetitionUser zelf
                        CompetitionUser::getNumericValues  // value is de lijst van numerieke waarden
                ));

        Map<CompetitionUser, Double> scoresMap = switch (ranking) {
            case BEST -> map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .max(Long::compareTo)
                                    .orElse(0L)  // Teruggeven als Long
                                    .doubleValue()  // Converteer Long naar Double
                    ));
            case AVERAGE -> map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .mapToLong(Long::longValue)
                                    .average()
                                    .orElse(0.0)  // Direct als Double
                    ));
            case AVERAGE_TOP_3 -> map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .sorted(Comparator.reverseOrder())
                                    .limit(3)
                                    .mapToLong(Long::longValue)
                                    .average()
                                    .orElse(0.0)  // Direct als Double
                    ));
        };
        scoresMap.forEach((u, value) -> {
            u.setCalculatedScore(value.toString());
        });
        List<Map.Entry<CompetitionUser, Double>> sortedEntries = scoresMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        int rank = 1;
        for (Map.Entry<CompetitionUser, Double> entry : sortedEntries) {
            CompetitionUser user = entry.getKey();
            user.setCompetitionRank(rank++);
        }

        repo.saveAll(
                sortedEntries.stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
        );


    }
}
