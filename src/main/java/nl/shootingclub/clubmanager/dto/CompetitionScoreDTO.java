package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CompetitionScoreDTO {
    @NotNull
    private UUID competitionID;
    @NotNull
    private UUID userID;

    @NotNull
    private long score;

    @NotNull
    private LocalDateTime scoreDate;

}
