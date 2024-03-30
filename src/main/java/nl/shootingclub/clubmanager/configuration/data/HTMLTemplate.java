package nl.shootingclub.clubmanager.configuration.data;

import lombok.Getter;

@Getter
public enum HTMLTemplate {
    REGISTERED("registered", "mail/test.html");

    private final String subject;
    private final String location;
    HTMLTemplate(String subject, String location) {
        this.subject = subject;
        this.location = location;

    }

}
