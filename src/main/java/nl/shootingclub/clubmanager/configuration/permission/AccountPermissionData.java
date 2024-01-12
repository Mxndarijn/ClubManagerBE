package nl.shootingclub.clubmanager.configuration.permission;

import lombok.Getter;

@Getter
public enum AccountPermissionData {
    GET_MY_ASSOCIATIONS("get-my-associations", "Permission to request all the associations that belong to the user."),
    GET_MY_PROFILE("get-my-profile", "Permission to request the users own profile."),
    GET_MY_PERMISSIONS("get-my-permissions", "Permission to request all the permissions the user has."),
    CREATE_ASSOCIATION("create-association", ""),
    GET_MY_ASSOCIATION_PERMISSIONS("get-my-association-permissions", "Permission to request all the permissions the user has on the associations.");

    private final String name;
    private final String description;
    AccountPermissionData(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
