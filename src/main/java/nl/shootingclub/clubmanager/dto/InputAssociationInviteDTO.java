package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InputAssociationInviteDTO {

    private UUID userUUID;
    private UUID associationUUID;
}
