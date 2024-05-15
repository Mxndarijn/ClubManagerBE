package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.data.CompetitionRanking;
import nl.shootingclub.clubmanager.configuration.data.CompetitionScoreType;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CompetitionDTO {

    @NotNull(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    private String name;

    @NotNull(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    private String description;

    @NotNull
    private CompetitionRanking competitionRanking;

    @NotNull
    private CompetitionScoreType competitionScoreType;

    @NotNull
    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDateTime startDate;

    @NotNull
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime endDate;
}
