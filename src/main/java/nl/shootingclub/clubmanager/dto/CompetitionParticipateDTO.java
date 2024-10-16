package nl.shootingclub.clubmanager.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompetitionParticipateDTO {

    @NotNull
    private UUID reservationID;

    @NotNull
    private boolean join;

    @Nullable
    private Integer position;
}
