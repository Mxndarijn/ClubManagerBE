package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

import java.util.List;

@Getter
public enum DefaultRoleAssociation {
    ADMIN("Admin", List.of(AssociationPermissionData.COMPETITION_SCORE_MANAGER, AssociationPermissionData.MANAGE_COMPETITIONS, AssociationPermissionData.MANAGE_MEMBERS, AssociationPermissionData.MANAGE_SETTINGS, AssociationPermissionData.MANAGE_TRACK_CONFIGURATION, AssociationPermissionData.MANAGE_WEAPONS, AssociationPermissionData.VIEW_TRACKS, AssociationPermissionData.VIEW_RESERVATIONS, AssociationPermissionData.VIEW_WEAPONS)),
    USER("User", List.of(AssociationPermissionData.VIEW_TRACKS, AssociationPermissionData.VIEW_RESERVATIONS, AssociationPermissionData.VIEW_WEAPONS)),
    VISITOR("Visitor", List.of(AssociationPermissionData.VIEW_TRACKS, AssociationPermissionData.VIEW_RESERVATIONS, AssociationPermissionData.VIEW_WEAPONS));

    private final String name;
    private final List<AssociationPermissionData> permissions;
    DefaultRoleAssociation(String name, List<AssociationPermissionData> permissions) {
        this.name = name;
        this.permissions = permissions;
    }
}
