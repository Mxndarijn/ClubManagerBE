package nl.shootingclub.clubmanager.configuration.role;

import lombok.Getter;

@Getter
public enum DefaultRoleAccount {
    ADMIN("Admin"),
    USER("User");

    private final String name;
    DefaultRoleAccount(String name) {
        this.name = name;
    }
}
