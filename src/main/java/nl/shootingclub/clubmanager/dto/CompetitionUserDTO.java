package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompetitionUserDTO {
    @NotNull
    private UUID competitionID;
    @NotNull
    private UUID userID;

}
