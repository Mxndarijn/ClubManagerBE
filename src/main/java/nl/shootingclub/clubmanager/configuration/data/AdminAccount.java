package nl.shootingclub.clubmanager.configuration.data;

public enum AdminAccount {
    ADMIN_1("b932d413-947c-44bc-a9ac-f540fcacacb2@mail.com", "b09c3bfd-cadd-481d-ab43-8b2aa234ce78@1", "Admin Name 1", "nl");
    private final String email;
    private final String password;
    private final String name;
    private final String language;

    // Constructor voor AdminAccount enum
    AdminAccount(String email, String password, String name, String language) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.language = language;
    }

    // Getters voor de eigenschappen
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }
}
