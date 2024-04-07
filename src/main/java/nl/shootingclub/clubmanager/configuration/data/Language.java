package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Language {
    NL("nl"),
    EN("en");

    private final String language;
    Language(String l) {
        this.language = l;
    }

    public static Optional<Language> fromString(String lang) {
        for (Language language : values()) {
            if (language.getLanguage().equals(lang)) {
                return Optional.of(language);
            }
        }
        return Optional.empty();
    }
}