package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultBooleanResponseDTO {

    private boolean success;
    private String message;
}
