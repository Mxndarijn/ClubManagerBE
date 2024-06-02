package nl.shootingclub.clubmanager;

import org.junit.jupiter.api.*;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.*;

public class AuthenticationTests {



    private HttpGraphQlTester graphQlTester;



    @BeforeEach
    public void setup() {
        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .build();

        this.graphQlTester = HttpGraphQlTester.create(client);
    }

    @Test
    public void testLoginWithFalseCredentials(){
        graphQlTester.documentName("login")
                .variable("loginRequest", Map.of("email", "testEmail@mail.com", "password", "WrongPassword"))
                .execute()
                .path("$.data.authenticationMutations.login.success").entity(Boolean.class).isEqualTo(false)
                .path("$.data.authenticationMutations.login.message").entity(String.class).matches(message -> message.equalsIgnoreCase("account-bad-credentials"));
    }

    @Test
    public void testRegisterAndLogin(){
        String email = UUID.randomUUID().toString() + "@mail.com";
        String password = "Password2!";

        graphQlTester.documentName("register")
                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.length() > 20);

        graphQlTester.documentName("login")
                .variable("loginRequest", Map.of("email", email, "password", password))
                .execute()
                .path("$.data.authenticationMutations.login.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.login.message").entity(String.class).matches(message -> message.length() > 20);
    }
//    @Test
//    @Order(1000)
//    public void testLoginRateLimiting(){
//        String email = UUID.randomUUID().toString() + "@mail.com";
//        String password = "Password2!";
//
//        graphQlTester.documentName("register")
//                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
//                .execute()
//                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(true)
//                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.length() > 20);
//
//        AtomicInteger wrongCounter = new AtomicInteger(0);
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                boolean isSuccessful = graphQlTester.documentName("login")
//                        .variable("loginRequest", Map.of("email", email, "password", password))
//                        .execute()
//                        .path("$.data.authenticationMutations.login.success").entity(Boolean.class).get();
//                if (!isSuccessful) {
//                    wrongCounter.incrementAndGet(); // Verhoog de teller veilig
//                }
//            });
//            futures.add(future);
//        }
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()-1])).join();
//
//        System.out.println(wrongCounter.get());
//        assertTrue(wrongCounter.get() > 40, "Er moeten meer dan 40 inlogpogingen falen");
//
//
//
//    }

    @Test
    public void testRegisterWithAlreadyExistingCredentials(){
        String email = UUID.randomUUID().toString() + "@mail.com";
        String password = "Password2!";

        graphQlTester.documentName("register")
                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.length() > 20);

        graphQlTester.documentName("register")
                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(false)
                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.equalsIgnoreCase("email-already-used"));
    }
    @Test
    public void testRegisterWithWrongEmail(){
        String email = UUID.randomUUID().toString() + "mail.com";
        String password = "Password2!";

        graphQlTester.documentName("register")
                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .errors()
                .expect(Objects::nonNull);
    }
    @Test
    public void testRegisterWithEasyPassword(){
        String email = UUID.randomUUID().toString() + "@mail.com";
        String password = "a!";

        graphQlTester.documentName("register")
                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .errors()
                .expect(Objects::nonNull);
    }
    @Test
    public void testValidateTokenWithCorrectToken(){
        String email = UUID.randomUUID().toString() + "@mail.com";
        String password = "Password2!";

        String token = graphQlTester.documentName("register")
                .variable("registerRequest", Map.of("email", email, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.length() > 20)
                .path("$.data.authenticationMutations.register.message").entity(String.class).get();

        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .defaultHeader("Authorization", "Bearer " + token)
                        .build();

        this.graphQlTester = HttpGraphQlTester.create(client);

        graphQlTester.documentName("validateToken")
                .execute()
                .path("$.data.authenticationQueries.validateToken.success").entity(Boolean.class).isEqualTo(true);

    }

    @Test
    public void testValidateTokenWithWrongToken(){

        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .defaultHeader("Authorization", UUID.randomUUID().toString())
                        .build();

        this.graphQlTester = HttpGraphQlTester.create(client);

        graphQlTester.documentName("validateToken")
                .execute()
                .path("$.data.authenticationQueries.validateToken.success").entity(Boolean.class).isEqualTo(false);


    }
}
