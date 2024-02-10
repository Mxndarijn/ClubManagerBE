package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.UserAssociation;

@Setter
@Getter
public class ChangeUserAssociationResponseDTO {

    private boolean success;
    private UserAssociation userAssociation;
}
