package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.AssociationPermission;
import nl.shootingclub.clubmanager.model.UserAssociation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class AssociationPermissionDTO {
    private List<String> permissions;
    private UUID associationUUID;
    private String associationName;

    public AssociationPermissionDTO(UserAssociation userAssociation) {
        this.permissions = userAssociation.getAssociationRole().getPermissions().stream()
                .map(AssociationPermission::getName)
                .collect(Collectors.toList());
        this.associationUUID = userAssociation.getAssociation().getId();
        this.associationName = userAssociation.getAssociation().getName();
    }
}