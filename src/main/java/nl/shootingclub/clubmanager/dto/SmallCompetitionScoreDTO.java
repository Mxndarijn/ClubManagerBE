package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SmallCompetitionScoreDTO {
    @NotNull
    private long score;

    @NotNull
    private LocalDate scoreDate;

}
