package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssociationInviteDTO {

    private UUID userUUID;
    private UUID associationUUID;
    private UUID associationRoleUUID;
}
