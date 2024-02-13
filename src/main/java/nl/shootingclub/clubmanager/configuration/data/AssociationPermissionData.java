package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

@Getter
public enum AssociationPermissionData {
    MANAGE_MEMBERS("manage-members", ""),
    MANAGE_TRACK_CONFIGURATION("manage-track-configuration", ""),
    MANAGE_COMPETITIONS("manage-competitions", "Permission to add and delete competitions"),
    MANAGE_SETTINGS("manage-settings", "Permission to manage all the settings"),
    MANAGE_WEAPONS("manage-weapons", "Permission to manage all the weapons"),
    VIEW_WEAPONS("view-weapons", "Permission to view all the weapons"),
    VIEW_TRACKS("view-tracks", "Permission to view all the tracks"),
    VIEW_RESERVATIONS("view-reservations", "Permission to view all the reservations of a association")


    ;

    private final String name;
    private final String description;
    AssociationPermissionData(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
