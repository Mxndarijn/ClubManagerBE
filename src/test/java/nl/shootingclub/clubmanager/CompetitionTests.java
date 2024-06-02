package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.model.AssociationCompetition;
import nl.shootingclub.clubmanager.model.AssociationInvite;
import nl.shootingclub.clubmanager.model.AssociationRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompetitionTests {

    private static HttpGraphQlTester graphQlTesterWithAdminAccount;
    private static HttpGraphQlTester graphQlTesterWithUserAccount;

    private static String associationID;

    private static String userID;


    @BeforeAll
    public static void setup() {
        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .build();
        HttpGraphQlTester tester = HttpGraphQlTester.create(client);
        String adminPassword = "b09c3bfd-cadd-481d-ab43-8b2aa234ce78@1";
        String adminEmail = "b932d413-947c-44bc-a9ac-f540fcacacb2@mail.com";
        String token = tester.documentName("login")
                .variable("loginRequest", Map.of("email", adminEmail, "password", adminPassword))
                .execute()
                .path("$.data.authenticationMutations.login.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.login.message").entity(String.class).matches(me -> me.length() > 20).get();
        client =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:8080/graphql")
                        .defaultHeader("Authorization", "Bearer " + token)
                        .build();

        graphQlTesterWithAdminAccount = HttpGraphQlTester.create(client);

        //Create association
        associationID = graphQlTesterWithAdminAccount.documentName("createAssociation")
                .execute()
                .path("$.data.createAssociation.id").hasValue().entity(String.class).get();

        String mail = UUID.randomUUID() + "@mail.com";
        String password = "Password2!";
        String userToken = tester.documentName("register")
                .variable("registerRequest", Map.of("email", mail, "password", password, "fullName", "Merijn Gommeren", "language", "nl"))
                .execute()
                .path("$.data.authenticationMutations.register.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.authenticationMutations.register.message").entity(String.class).matches(message -> message.length() > 20).get();

        WebTestClient client1 =
            WebTestClient.bindToServer()
                    .baseUrl("http://localhost:8080/graphql")
                    .defaultHeader("Authorization", "Bearer " + userToken)
                    .build();
        graphQlTesterWithUserAccount = HttpGraphQlTester.create(client1);

        userID = graphQlTesterWithUserAccount.documentName("getMyID")
                .execute()
                .path("$.data.userQueries.getMyProfile.id").hasValue().entity(String.class).get();

        //Create more users
        List<AssociationRole> roles = graphQlTesterWithAdminAccount.documentName("getAssociationRoles")
                        .execute()
                .path("$.data.utilQueries.getAssociationRoles").hasValue().entityList(AssociationRole.class).get();
        Optional<AssociationRole> userRoleOptional = roles.stream().filter(r -> {
            return r.getName().equalsIgnoreCase("User");

        }).findFirst();
        if(userRoleOptional.isEmpty()) {
            return;
        }

        //Add user to association
        graphQlTesterWithAdminAccount.documentName("sendAssociationInvite")
                .variable("dto", Map.of("userEmail", mail, "associationUUID", associationID, "associationRoleUUID", userRoleOptional.get().getId()))
                .execute()
                .path("$.data.associationMutations.associationMemberMutations.sendAssociationInvite.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.associationMutations.associationMemberMutations.sendAssociationInvite.associationInvite").hasValue();

        graphQlTesterWithUserAccount.documentName("acceptAssociationInvite")
                .variable("inviteId", Map.of("userUUID", userID, "associationUUID", associationID))
                .execute()
                .path("$.data.associationMutations.associationMemberMutations.acceptAssociationInvite.message").hasValue()
                .path("$.data.associationMutations.associationMemberMutations.acceptAssociationInvite.success").entity(Boolean.class).isEqualTo(true);

    }

    @Test
    public void createCompetition() {
        String compID = graphQlTesterWithAdminAccount.documentName("createCompetition")
                .variable("dto", Map.of("name", "Test", "description", "TestDescription","competitionRanking", "BEST", "competitionScoreType", "TIME", "startDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "endDate", LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .variable("associationID", associationID)
                .execute()
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.competition").hasValue()
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.competition.id").hasValue().entity(String.class).get();
    }

    @Test
    public void addUserToCompetition() {
        String compID = graphQlTesterWithAdminAccount.documentName("createCompetition")
                .variable("dto", Map.of("name", "Test", "description", "TestDescription","competitionRanking", "BEST", "competitionScoreType", "TIME", "startDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "endDate", LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .variable("associationID", associationID)
                .execute()
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.competition").hasValue()
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.competition.id").hasValue().entity(String.class).get();

    graphQlTesterWithAdminAccount.documentName("addUserToCompetition")
            .variable("dto", Map.of("userID", userID, "competitionID", compID))
            .variable("associationID", associationID)
            .execute()
            .path("$.data.associationMutations.associationCompetitionMutations.addUser.success").entity(Boolean.class).isEqualTo(true)
    ;
    }

    @Test
    public void getCompetitionInfo() {
        String compName = "Test";
        String compDescription = "Description";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMonths(1);
        String compID = graphQlTesterWithAdminAccount.documentName("createCompetition")
                .variable("dto", Map.of("name", compName, "description", compDescription,"competitionRanking", "BEST", "competitionScoreType", "TIME", "startDate", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "endDate", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .variable("associationID", associationID)
                .execute()
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.success").entity(Boolean.class).isEqualTo(true)
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.competition").hasValue()
                .path("$.data.associationMutations.associationCompetitionMutations.createCompetition.competition.id").hasValue().entity(String.class).get();

        List<AssociationCompetition> comps = graphQlTesterWithAdminAccount.documentName("getCompetitionInfo")
                .variable("associationID", associationID)
                .execute()
                .path("$.data.associationQueries.getAssociationDetails.competitions").hasValue().entityList(AssociationCompetition.class).get();

        Optional<AssociationCompetition> competitionOptional = comps.stream().filter(r -> {
            return r.getId().toString().equalsIgnoreCase(compID);

        }).findFirst();
        assertTrue(competitionOptional.isPresent(), "Could not find Competition");
        AssociationCompetition competition = competitionOptional.get();
        assertTrue(competition.getName().equalsIgnoreCase(compName));
        assertTrue(competition.getDescription().equalsIgnoreCase(compDescription));
        assertTrue(competition.getRanking().toString().equalsIgnoreCase("BEST"));
        assertTrue(competition.getScoreType().toString().equalsIgnoreCase("TIME"));
        assertEquals(competition.getStartDate().truncatedTo(ChronoUnit.SECONDS), startTime.truncatedTo(ChronoUnit.SECONDS));
        assertEquals(competition.getEndDate().truncatedTo(ChronoUnit.SECONDS), endTime.truncatedTo(ChronoUnit.SECONDS));
        assertTrue(competition.isActive());

    }



}
