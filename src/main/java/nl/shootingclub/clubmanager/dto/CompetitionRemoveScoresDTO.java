package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CompetitionRemoveScoresDTO {
    @NotNull
    private UUID competitionID;
    @NotNull
    private UUID userID;

    @NotNull
    private List<UUID> scores;

}
