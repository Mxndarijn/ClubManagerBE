package nl.shootingclub.clubmanager.configuration.weapons;

import lombok.Getter;

@Getter
public enum DefaultWeaponType {
    BIAT("BIAT"),
    LG("LG"),
    lP("LP"),
    ;


    private final String name;
    DefaultWeaponType(String name) {
        this.name = name;
    }
}
