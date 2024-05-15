package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Competition;

@Getter
@Setter
public class CompetitionResponseDTO {

    private boolean success;
    private String message;

    private Competition competition;
}
