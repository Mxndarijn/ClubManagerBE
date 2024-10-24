package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

@Getter
public enum AccountPermissionData {
    GET_MY_PROFILE("get-my-profile", "Permission to request the user's own profile."),
    CREATE_ASSOCIATION("create-association", ""),
    GET_ASSOCIATION_ROLES("get-asociation-roles", ""),
    ACCEPT_ASSOCIATION_INVITE("accept-association-invite", ""),
    ;

    private final String name;
    private final String description;
    AccountPermissionData(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
