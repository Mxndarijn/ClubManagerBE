package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.model.UserAssociation;

@Setter
@Getter
public class CreateTrackResponseDTO {

    private boolean success;
    private String message;
    private Track track;
}
