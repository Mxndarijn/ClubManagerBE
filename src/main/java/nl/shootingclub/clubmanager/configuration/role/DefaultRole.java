package nl.shootingclub.clubmanager.configuration.role;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum DefaultRole {
    ADMIN("Admin"),
    USER("User");

    private final String name;
    DefaultRole(String name) {
        this.name = name;
    }
}
