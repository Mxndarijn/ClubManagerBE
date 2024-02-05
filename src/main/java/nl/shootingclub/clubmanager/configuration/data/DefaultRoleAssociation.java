package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

@Getter
public enum DefaultRoleAssociation {
    ADMIN("Admin"),
    USER("User"),
    VISITOR("Visitor");

    private final String name;
    DefaultRoleAssociation(String name) {
        this.name = name;
    }
}
