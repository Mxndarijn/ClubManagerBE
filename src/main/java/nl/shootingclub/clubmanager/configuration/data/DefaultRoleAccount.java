package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

import java.util.List;

@Getter
public enum DefaultRoleAccount {
    ADMIN("Admin", List.of(AccountPermissionData.GET_MY_PROFILE, AccountPermissionData.CREATE_ASSOCIATION, AccountPermissionData.GET_ASSOCIATION_ROLES)),
    USER("User", List.of(AccountPermissionData.GET_MY_PROFILE, AccountPermissionData.GET_ASSOCIATION_ROLES));

    private final String name;
    private final List<AccountPermissionData> permissions;
    DefaultRoleAccount(String name, List<AccountPermissionData> permissions) {
        this.name = name;
        this.permissions = permissions;
    }
}
