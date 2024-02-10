package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.AssociationInvite;
import nl.shootingclub.clubmanager.model.AssociationInviteId;
import nl.shootingclub.clubmanager.model.UserAssociation;

@Setter
@Getter
public class SendAssociationInviteResponseDTO {

    private boolean success;
    private String message;
    private AssociationInvite associationInvite;
}
