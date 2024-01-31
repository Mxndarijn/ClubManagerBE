package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum DefaultColorPreset {
    BLUE("config.colors.blue", "#0D028C", "#BFB8DB"),
    RED("config.colors.red", "#8C0202", "#DBB8B8"),
    YELLOW("config.colors.yellow", "#D9DD13", "#DBD9B8"),
    GREEN("config.colors.green", "#028C20", "#BADBB8"),

    ;

    private final String name;
    private final String primary;
    private final String secondary;
    DefaultColorPreset(String name, String primary, String secondary) {
        this.name = name;
        this.primary = primary;
        this.secondary = secondary;
    }
}
