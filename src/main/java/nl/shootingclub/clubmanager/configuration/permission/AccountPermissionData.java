package nl.shootingclub.clubmanager.configuration.permission;

import lombok.Getter;

@Getter
public enum AccountPermissionData {
    DEFAULT_GET_MY_ASSOCIATIONS("get-my-associations", "Permission to request all the associations that belong to the user.");

    private final String name;
    private final String description;
    AccountPermissionData(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
