package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultBooleanResponseDTO {

    private boolean success;
    private String message;
}
