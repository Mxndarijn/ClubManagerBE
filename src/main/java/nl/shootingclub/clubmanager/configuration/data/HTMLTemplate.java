package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

import java.io.File;

@Getter
public enum HTMLTemplate {
    REGISTERED("test");

    private final String location;
    HTMLTemplate(String location) {
        this.location = location;

    }

    public String getLocation(Language language) {
        return "mail" + File.separator + language.getLanguage() + File.separator + location;

    }
}
