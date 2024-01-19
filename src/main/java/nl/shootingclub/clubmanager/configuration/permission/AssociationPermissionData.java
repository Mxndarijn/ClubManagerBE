package nl.shootingclub.clubmanager.configuration.permission;

import lombok.Getter;

@Getter
public enum AssociationPermissionData {
    MANAGE_MEMBERS("manage-members", ""),
    MANAGE_TRACK_CONFIGURATION("manage-track-configuration", ""),
    MANAGE_COMPETITIONS("manage-competitions", ""),
    MANAGE_SETTINGS("manage-settings", ""),
    MANAGE_WEAPONS("manage-weapons", ""),
    VIEW_WEAPONS("view-weapons", ""),


    ;

    private final String name;
    private final String description;
    AssociationPermissionData(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
