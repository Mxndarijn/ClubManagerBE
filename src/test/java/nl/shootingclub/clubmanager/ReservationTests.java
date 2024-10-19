package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.dto.ColorPresetDTO;
import nl.shootingclub.clubmanager.dto.WeaponTypeDTO;
import nl.shootingclub.clubmanager.dto.response.*;
import nl.shootingclub.clubmanager.model.AssociationRole;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationTests {

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
        List<WeaponTypeDTO> weaponTypes = graphQlTesterWithAdminAccount.documentName("getAllWeaponTypes")
                .execute()
                .path("$.data.utilQueries.getAllWeaponTypes").hasValue().entityList(WeaponTypeDTO.class).get();
        Assertions.assertFalse(weaponTypes.isEmpty(), "weaponTypes list should not be empty.");
//        graphQlTesterWithAdminAccount.documentName("createTrack")
//                .variable("associationID", associationID)
//                .variable("dto", Map.of("name", "TestTrack", "description", "TestDescription", "allowedWeaponTypes", List.of(weaponTypes.get(0).getId())))
//                .execute();

    }

    @Test
    public void testCreateReservation() {
        createReservation();
    }

    @Test
    public void testParticipationInReservation() {
        Reservation reservation = createReservation();

        String reservationID = reservation.getId().toString();

        ReservationResponseDTO participateReservationResponse = graphQlTesterWithUserAccount.documentName("participateReservation")
                .variable("associationID", associationID)
                .variable("reservationID", reservationID)
                .variable("join", true)
                .variable("position", -1)
                .execute()
                .path("$.data.associationMutations.associationReservationMutations.participateReservation")
                .entity(ReservationResponseDTO.class)
                .get();
        
        Assertions.assertTrue(participateReservationResponse.isSuccess(), "Should return success.");
        Assertions.assertEquals(1, participateReservationResponse.getReservation().getReservationUsers().size(), "List should contain one reservation user.");
        ReservationUser reservationUser = participateReservationResponse.getReservation().getReservationUsers().iterator().next();

// Assertion for reservation ID
        Assertions.assertNotNull(reservationUser.getId(), "Reservation User ID should not be null.");

// Assertion for user
        Assertions.assertNotNull(reservationUser.getUser(), "User should not be null.");
//        Assertions.assertEquals(userID, reservationUser.getUser().getId(), "User ID should match the expected value.");

// Assertion for register date
        Assertions.assertNotNull(reservationUser.getRegisterDate(), "Register date should not be null.");

// Assertion for user position
        Assertions.assertNotNull(reservationUser.getPosition(), "User position should not be null.");
        Assertions.assertTrue(reservationUser.getPosition() >= 0, "User position should be greater than 0.");

        GetReservationResponseDTO checkResponse = graphQlTesterWithAdminAccount.documentName("getReservationsBetween")
            .variable("associationID", associationID)
            .variable("startTime", reservation.getStartDate().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .variable("endTime", reservation.getStartDate().plusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .execute()
            .path("$.data.associationQueries.associationReservationQueries.getReservationsBetween")
            .entity(GetReservationResponseDTO.class)
            .get();

        Assertions.assertFalse(checkResponse.getReservations().isEmpty(), "The list should contain at least one reservation.");
        Reservation reservationToCheck = checkResponse.getReservations().stream().filter(r -> r.getId().toString().equals(reservationID)).findFirst().get();
        Assertions.assertEquals(1, reservationToCheck.getReservationUsers().size(), "List should contain one reservation user.");
        reservationUser = reservationToCheck.getReservationUsers().iterator().next();

        // Assertion for reservation ID
        Assertions.assertNotNull(reservationUser.getId(), "Reservation User ID should not be null.");

// Assertion for user
        Assertions.assertNotNull(reservationUser.getUser(), "User should not be null.");
//        Assertions.assertEquals(userID, reservationUser.getUser().getId(), "User ID should match the expected value.");

// Assertion for register date
        Assertions.assertNotNull(reservationUser.getRegisterDate(), "Register date should not be null.");

// Assertion for user position
        Assertions.assertNotNull(reservationUser.getPosition(), "User position should not be null.");
        Assertions.assertTrue(reservationUser.getPosition() >= 0, "User position should be greater than 0.");





// Additional assertions depending on application-specific checks
// Assertions.assertEquals(expectedValue, reservationUser.getSomeField(), "Some field should match the expected value.");

    }

    @Test
    public void testLeaveParticipationInReservation() {
       Reservation reservation = createReservation();
       String reservationID = reservation.getId().toString();

        ReservationResponseDTO participateReservationResponse = graphQlTesterWithUserAccount.documentName("participateReservation")
                .variable("associationID", associationID)
                .variable("reservationID", reservationID)
                .variable("join", true)
                .variable("position", -1)
                .execute()
                .path("$.data.associationMutations.associationReservationMutations.participateReservation")
                .entity(ReservationResponseDTO.class)
                .get();

        Assertions.assertTrue(participateReservationResponse.isSuccess(), "Should return success.");
        Assertions.assertEquals(1, participateReservationResponse.getReservation().getReservationUsers().size(), "List should contain one reservation user.");
        ReservationUser reservationUser = participateReservationResponse.getReservation().getReservationUsers().iterator().next();

// Assertion for reservation ID
        Assertions.assertNotNull(reservationUser.getId(), "Reservation User ID should not be null.");

// Assertion for user
        Assertions.assertNotNull(reservationUser.getUser(), "User should not be null.");
//        Assertions.assertEquals(userID, reservationUser.getUser().getId(), "User ID should match the expected value.");

// Assertion for register date
        Assertions.assertNotNull(reservationUser.getRegisterDate(), "Register date should not be null.");

// Assertion for user position
        Assertions.assertNotNull(reservationUser.getPosition(), "User position should not be null.");
        Assertions.assertTrue(reservationUser.getPosition() >= 0, "User position should be greater than 0.");

        GetReservationResponseDTO checkResponse = graphQlTesterWithAdminAccount.documentName("getReservationsBetween")
                .variable("associationID", associationID)
                .variable("startTime", reservation.getStartDate().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .variable("endTime", reservation.getStartDate().plusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .execute()
                .path("$.data.associationQueries.associationReservationQueries.getReservationsBetween")
                .entity(GetReservationResponseDTO.class)
                .get();

        Assertions.assertEquals(1, checkResponse.getReservations().size(), "List should contain one reservation.");
        Reservation reservationToCheck = checkResponse.getReservations().stream().filter(r -> r.getId().toString().equals(reservationID)).findFirst().get();
        Assertions.assertEquals(1, reservationToCheck.getReservationUsers().size(), "List should contain one reservation user.");
        reservationUser = reservationToCheck.getReservationUsers().iterator().next();

        // Assertion for reservation ID
        Assertions.assertNotNull(reservationUser.getId(), "Reservation User ID should not be null.");

// Assertion for user
        Assertions.assertNotNull(reservationUser.getUser(), "User should not be null.");
//        Assertions.assertEquals(userID, reservationUser.getUser().getId(), "User ID should match the expected value.");

// Assertion for register date
        Assertions.assertNotNull(reservationUser.getRegisterDate(), "Register date should not be null.");

// Assertion for user position
        Assertions.assertNotNull(reservationUser.getPosition(), "User position should not be null.");
        Assertions.assertTrue(reservationUser.getPosition() >= 0, "User position should be greater than 0.");

        ReservationResponseDTO leaveParticipateReservationResponse = graphQlTesterWithUserAccount.documentName("participateReservation")
                .variable("associationID", associationID)
                .variable("reservationID", reservationID)
                .variable("join", false)
                .variable("position", -1)
                .execute()
                .path("$.data.associationMutations.associationReservationMutations.participateReservation")
                .entity(ReservationResponseDTO.class)
                .get();

        Assertions.assertEquals(0, leaveParticipateReservationResponse.getReservation().getReservationUsers().size(), "List should contain one reservation.");

        GetReservationResponseDTO checkResponse1 = graphQlTesterWithAdminAccount.documentName("getReservationsBetween")
                .variable("associationID", associationID)
                .variable("startTime", reservation.getStartDate().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .variable("endTime", reservation.getStartDate().plusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .execute()
                .path("$.data.associationQueries.associationReservationQueries.getReservationsBetween")
                .entity(GetReservationResponseDTO.class)
                .get();

        Assertions.assertTrue(checkResponse1.getReservations().size() >=0, "List should contain one reservation.");
        Reservation reservationToCheck1 = checkResponse1.getReservations().iterator().next();
        Assertions.assertEquals(0, reservationToCheck1.getReservationUsers().size(), "List should contain one reservation user.");




// Additional assertions depending on application-specific checks
// Assertions.assertEquals(expectedValue, reservationUser.getSomeField(), "Some field should match the expected value.");

    }

    public Reservation createReservation() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String title = random.ints(20, 'A', 'Z' + 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String description = random.ints(32, 'A', 'Z' + 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        List<ColorPresetDTO> colorPresets = graphQlTesterWithAdminAccount.documentName("getColorPresets")
                .execute()
                .path("$.data.utilQueries.getAllColorPresets").hasValue().entityList(ColorPresetDTO.class).get();

        List<WeaponTypeDTO> weaponTypes = graphQlTesterWithAdminAccount.documentName("getAllWeaponTypes")
                .execute()
                .path("$.data.utilQueries.getAllWeaponTypes").hasValue().entityList(WeaponTypeDTO.class).get();

        graphQlTesterWithAdminAccount.documentName("createTrack")
                .variable("associationID", associationID)
                .variable("dto", Map.of("name", "TestTrack", "description", "TestDescription", "allowedWeaponTypes", List.of(weaponTypes.get(0).getId().toString())))
                .execute();

        List<TrackDTOFull> tracks = graphQlTesterWithAdminAccount.documentName("getAllTracks")
                .variable("associationID", associationID)
                .execute()
                .path("$.data.associationQueries.associationTrackQueries.getTracksOfAssociation").hasValue().entityList(TrackDTOFull.class).get();

        Assertions.assertFalse(colorPresets.isEmpty(), "ColorPresets list should not be empty.");
        Assertions.assertFalse(weaponTypes.isEmpty(), "weaponTypes list should not be empty.");
        Assertions.assertFalse(tracks.isEmpty(), "tracks list should not be empty.");

        LocalDateTime startDate = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        System.out.println(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        LocalDateTime endDate = startDate.withHour(14).withMinute(0).withSecond(0).withNano(0);
        System.out.println(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Map<String, Object> dto = new HashMap<>();
        dto.put("title", title);
        dto.put("description", description);
        dto.put("startTime", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.put("endTime", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.put("maxSize", "2");
        dto.put("repeatType", "NO_REPEAT");
        dto.put("associationID", associationID);
        dto.put("userCanChooseOwnPosition", false);
        dto.put("repeatUntil", "");
        dto.put("colorPreset", colorPresets.get(0).getId().toString());
        dto.put("allowedWeaponTypes", List.of(weaponTypes.get(0).getId().toString()));
        dto.put("tracks", List.of(tracks.get(0).getId().toString()));

        CreateReservationResponseDTO response = graphQlTesterWithAdminAccount.documentName("createReservation")
                .variable("dto", dto)
                .execute()
                .path("$.data.associationMutations.associationReservationMutations.createReservations")
                .entity(CreateReservationResponseDTO.class)
                .get();
        Assertions.assertTrue(response.isSuccess(), "Should return success.");
        assertTrue(response.getReservations().size() >=0, "List should contain one reservation.");

        // Extra regels om de eerste reservering op te halen
        Reservation reservation = response.getReservations().iterator().next();

// 1. **Reservation ID Niet Null of Leeg**
        Assertions.assertNotNull(reservation.getId(), "Reservation ID should not be null.");
        Assertions.assertFalse(reservation.getId().toString().isEmpty(), "Reservation ID should not be empty.");

// 2. **Titel en Beschrijving**
        Assertions.assertEquals(title, reservation.getTitle(), "Titles should match.");
        Assertions.assertEquals(description, reservation.getDescription(), "Descriptions should match.");

// 3. **Start- en Eindtijd**
        LocalDateTime expectedStartTime = LocalDateTime.parse((String) dto.get("startTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedEndTime = LocalDateTime.parse((String) dto.get("endTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Assertions.assertEquals(expectedStartTime, reservation.getStartDate(), "Start times should match.");
        Assertions.assertEquals(expectedEndTime, reservation.getEndDate(), "End times should match.");

// 4. **Maximale Grootte en Herhalingstype**
        Assertions.assertEquals(Integer.parseInt((String) dto.get("maxSize")), reservation.getMaxSize(), "Max sizes should match.");

// 5. **Association ID**
        Assertions.assertNotNull(reservation.getAssociation(), "Association should not be null.");
        Assertions.assertEquals(UUID.fromString(associationID), reservation.getAssociation().getId(), "Association IDs should match.");

// 6. **Gebruiker Kan Zelf Positie Kiezen**
        Assertions.assertEquals(dto.get("userCanChooseOwnPosition"), reservation.isMembersCanChooseTheirOwnPosition(), "User position choice flag should match.");


// 8. **Color Preset**
        Assertions.assertNotNull(reservation.getColorPreset(), "ColorPreset should not be null.");
        Assertions.assertEquals(UUID.fromString((String) dto.get("colorPreset")), reservation.getColorPreset().getId(), "ColorPreset IDs should match.");
// Optioneel: Controleer andere kleurvelden als je die hebt
// Assertions.assertEquals("Expected Color Name", reservation.getColorPreset().getColorName(), "Color names should match.");

// 9. **Allowed Weapon Types**
        Assertions.assertNotNull(reservation.getAllowedWeaponTypes(), "AllowedWeaponTypes should not be null.");
        Assertions.assertFalse(reservation.getAllowedWeaponTypes().isEmpty(), "AllowedWeaponTypes should not be empty.");
// Controleer of de lijst de verwachte ID bevat
        Assertions.assertTrue(reservation.getAllowedWeaponTypes().stream()
                        .anyMatch(wt -> wt.getId().equals(weaponTypes.get(0).getId())),
                "AllowedWeaponTypes should contain the expected weapon type ID.");

// 10. **Tracks**
        Assertions.assertNotNull(reservation.getTracks(), "Tracks should not be null.");
        Assertions.assertFalse(reservation.getTracks().isEmpty(), "Tracks should not be empty.");
// Controleer of de lijst de verwachte ID bevat
        Assertions.assertTrue(reservation.getTracks().stream()
                        .anyMatch(track -> track.getId().equals(tracks.get(0).getId())),
                "Tracks should contain the expected track ID.");

// 11. **Reservation Users**
        Assertions.assertNotNull(reservation.getReservationUsers(), "ReservationUsers should not be null.");
// Afhankelijk van je setup, kun je verdere controles toevoegen
// Bijvoorbeeld, controleer dat er geen gebruikers zijn bij een nieuwe reservering
        Assertions.assertTrue(reservation.getReservationUsers().isEmpty(), "ReservationUsers should be empty for a new reservation.");

// 13. **Datumvaliditeit**
        Assertions.assertTrue(reservation.getEndDate().isAfter(reservation.getStartDate()), "End date should be after start date.");

        String reservationID = reservation.getId().toString();

        GetReservationResponseDTO checkResponse = graphQlTesterWithAdminAccount.documentName("getReservationsBetween")
                .variable("associationID", associationID)
                .variable("startTime", startDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .variable("endTime", startDate.plusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .execute()
                .path("$.data.associationQueries.associationReservationQueries.getReservationsBetween")
                .entity(GetReservationResponseDTO.class)
                .get();

        Optional<Reservation> optionalReservation = checkResponse.getReservations().stream().filter(r -> r.getId().toString().equals(reservationID)).findFirst();
        Assertions.assertTrue(optionalReservation.isPresent());

        reservation = optionalReservation.get();

        // 1. **Reservation ID Niet Null of Leeg**
        Assertions.assertNotNull(reservation.getId(), "Reservation ID should not be null.");
        Assertions.assertFalse(reservation.getId().toString().isEmpty(), "Reservation ID should not be empty.");

// 2. **Titel en Beschrijving**
        Assertions.assertEquals(title, reservation.getTitle(), "Titles should match.");
        Assertions.assertEquals(description, reservation.getDescription(), "Descriptions should match.");

// 3. **Start- en Eindtijd**

        Assertions.assertEquals(expectedStartTime, reservation.getStartDate(), "Start times should match.");
        Assertions.assertEquals(expectedEndTime, reservation.getEndDate(), "End times should match.");

// 4. **Maximale Grootte en Herhalingstype**
        Assertions.assertEquals(Integer.parseInt((String) dto.get("maxSize")), reservation.getMaxSize(), "Max sizes should match.");

// 5. **Association ID**
        Assertions.assertNotNull(reservation.getAssociation(), "Association should not be null.");
        Assertions.assertEquals(UUID.fromString(associationID), reservation.getAssociation().getId(), "Association IDs should match.");

// 6. **Gebruiker Kan Zelf Positie Kiezen**
        Assertions.assertEquals(dto.get("userCanChooseOwnPosition"), reservation.isMembersCanChooseTheirOwnPosition(), "User position choice flag should match.");


// 8. **Color Preset**
        Assertions.assertNotNull(reservation.getColorPreset(), "ColorPreset should not be null.");
        Assertions.assertEquals(UUID.fromString((String) dto.get("colorPreset")), reservation.getColorPreset().getId(), "ColorPreset IDs should match.");
// Optioneel: Controleer andere kleurvelden als je die hebt
// Assertions.assertEquals("Expected Color Name", reservation.getColorPreset().getColorName(), "Color names should match.");

// 9. **Allowed Weapon Types**
        Assertions.assertNotNull(reservation.getAllowedWeaponTypes(), "AllowedWeaponTypes should not be null.");
        Assertions.assertFalse(reservation.getAllowedWeaponTypes().isEmpty(), "AllowedWeaponTypes should not be empty.");
// Controleer of de lijst de verwachte ID bevat
        Assertions.assertTrue(reservation.getAllowedWeaponTypes().stream()
                        .anyMatch(wt -> wt.getId().equals(weaponTypes.get(0).getId())),
                "AllowedWeaponTypes should contain the expected weapon type ID.");

// 10. **Tracks**
        Assertions.assertNotNull(reservation.getTracks(), "Tracks should not be null.");
        Assertions.assertFalse(reservation.getTracks().isEmpty(), "Tracks should not be empty.");
// Controleer of de lijst de verwachte ID bevat
        Assertions.assertTrue(reservation.getTracks().stream()
                        .anyMatch(track -> track.getId().equals(tracks.get(0).getId())),
                "Tracks should contain the expected track ID.");

// 11. **Reservation Users**
        Assertions.assertNotNull(reservation.getReservationUsers(), "ReservationUsers should not be null.");
// Afhankelijk van je setup, kun je verdere controles toevoegen
// Bijvoorbeeld, controleer dat er geen gebruikers zijn bij een nieuwe reservering
        Assertions.assertTrue(reservation.getReservationUsers().isEmpty(), "ReservationUsers should be empty for a new reservation.");

// 13. **Datumvaliditeit**
        Assertions.assertTrue(reservation.getEndDate().isAfter(reservation.getStartDate()), "End date should be after start date.");

        return reservation;

    }

    @Test
    public void testDeleteReservation() {
        Reservation reservation = createReservation();
        String reservationID = reservation.getId().toString();

        DefaultBooleanResponseDTO checkResponse1 = graphQlTesterWithAdminAccount.documentName("deleteReservation")
                .variable("associationID", associationID)
                .variable("reservationID", reservationID)
                .execute()
                .path("$.data.associationMutations.associationReservationMutations.deleteReservation")
                .entity(DefaultBooleanResponseDTO.class)
                .get();


        Assertions.assertNotNull(checkResponse1, "Response should not be null.");
        Assertions.assertTrue(checkResponse1.isSuccess(), "Response should indicate success.");

        GetReservationResponseDTO checkResponse2 = graphQlTesterWithAdminAccount.documentName("getReservationsBetween")
                .variable("associationID", associationID)
                .variable("startTime", reservation.getStartDate().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .variable("endTime", reservation.getEndDate().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .execute()
                .path("$.data.associationQueries.associationReservationQueries.getReservationsBetween")
                .entity(GetReservationResponseDTO.class)
                .get();

        Optional<Reservation> deletedReservation = checkResponse2.getReservations().stream()
                .filter(r -> r.getId().toString().equals(reservationID))
                .findFirst();

        Assertions.assertTrue(deletedReservation.isEmpty(), "Deleted reservation should not be present in the list.");



    }

    @Test
    public void testDeleteNonExistingReservation() {

        DefaultBooleanResponseDTO checkResponse1 = graphQlTesterWithAdminAccount.documentName("deleteReservation")
                .variable("associationID", associationID)
                .variable("reservationID", UUID.randomUUID().toString())
                .execute()
                .path("$.data.associationMutations.associationReservationMutations.deleteReservation")
                .entity(DefaultBooleanResponseDTO.class)
                .get();


        Assertions.assertNotNull(checkResponse1, "Response should not be null.");
        Assertions.assertFalse(checkResponse1.isSuccess(), "Response should indicate failure for non-existing reservation.");



    }







}
