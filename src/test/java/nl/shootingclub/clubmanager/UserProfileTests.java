package nl.shootingclub.clubmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserProfileTests {

    private HttpGraphQlTester graphQlTester;
    private static String sharedEmail;
    private static String sharedPassword = "Passsword2!";
    private static final String sharedFullName = "Merijn Gommeren";
    private static final String sharedLanguage = "nl";


    @BeforeEach
    public void setup() {
        sharedEmail = UUID.randomUUID() + "@mail.com";
        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .build();
        HttpGraphQlTester tester = HttpGraphQlTester.create(client);
        String token = tester.documentName("register")
                .variable("registerRequest", Map.of("email", sharedEmail, "password", sharedPassword, "fullName", sharedFullName, "language", sharedLanguage))
                .execute()
                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.length() > 20).get();
        client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .defaultHeader("Authorization", "Bearer " + token)
                        .build();

        this.graphQlTester = HttpGraphQlTester.create(client);
    }

    @Test
    public void testProfileName() {
        graphQlTester.documentName("getMyProfile")
                .execute()
                .path("$.data.userQueries.getMyProfile.fullName").entity(String.class).matches(me -> me.equals(sharedFullName));
    }

    @Test
    public void testProfileEmail() {

        graphQlTester.documentName("getMyProfile")
                .execute()
                .path("$.data.userQueries.getMyProfile.email").entity(String.class).matches(me -> me.equals(sharedEmail));
    }

    @Test
    public void testProfileLanguage() {

        graphQlTester.documentName("getMyProfile")
                .execute()
                .path("$.data.userQueries.getMyProfile.language").entity(String.class).matches(me -> me.equals(sharedLanguage));
    }

    @Test
    public void testProfileAccount() {

        graphQlTester.documentName("getMyProfile")
                .execute()
                .path("$.data.userQueries.getMyProfile.role.name").entity(String.class).matches(me -> me.equals("User"));
    }

    @Test
    public void testChangeProfileFullName() {
        String name = "NewUser Name";

        graphQlTester.documentName("updateMyProfile")
                .variable("dto", Map.of("email", sharedEmail, "oldPassword", sharedPassword, "fullName", name))
                .execute();

        graphQlTester.documentName("getMyProfile")
                .execute()
                .path("$.data.userQueries.getMyProfile.fullName").entity(String.class).matches(me -> me.equals(name));
    }

    @Test
    public void testChangeProfileEmail() {
        String email = UUID.randomUUID().toString() + "@mail.com";

        graphQlTester.documentName("updateMyProfile")
                .variable("dto", Map.of("email", email, "oldPassword", sharedPassword, "fullName", sharedEmail))
                .execute();

        String token = graphQlTester.documentName("login")
                .variable("loginRequest", Map.of("email", email, "password", sharedPassword))
                .execute()
                .path("$.data.authenticationMutations.login.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.login.message").entity(String.class).matches(message -> message.length() > 20).get();

        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .defaultHeader("Authorization", "Bearer " + token)
                        .build();

        this.graphQlTester = HttpGraphQlTester.create(client);

        graphQlTester.documentName("getMyProfile")
                .execute()
                .path("$.data.userQueries.getMyProfile.email").entity(String.class).matches(me -> me.equals(email));
    }

    @Test
    public void testChangeProfilePassword() {
        String newPassword = UUID.randomUUID().toString() + "!23@";

        graphQlTester.documentName("updateMyProfile")
                .variable("dto", Map.of("email", sharedEmail, "oldPassword", sharedPassword, "fullName", sharedEmail, "newPassword", newPassword))
                .execute();

        graphQlTester.documentName("login")
            .variable("loginRequest", Map.of("email", sharedEmail, "password", newPassword))
            .execute()
            .path("$.data.authenticationMutations.login.success").entity(Boolean.class).isEqualTo(true)
            .path("$.data.authenticationMutations.login.message").entity(String.class).matches(message -> message.length() > 20).get();

        graphQlTester.documentName("login")
                .variable("loginRequest", Map.of("email", sharedEmail, "password", sharedPassword))
                .execute()
                .path("$.data.authenticationMutations.login.success").entity(Boolean.class).isEqualTo(false);

    }

}
