package nl.shootingclub.clubmanager.configuration;

import graphql.schema.idl.TypeRuntimeWiring;
import nl.shootingclub.clubmanager.configuration.datafetcher.association.*;
import nl.shootingclub.clubmanager.configuration.datafetcher.association.reservation.AssociationReservationUsersDataFetcher;
import nl.shootingclub.clubmanager.configuration.datafetcher.user.UserFullNameDataFetcher;
import nl.shootingclub.clubmanager.configuration.datafetcher.user.UserImageDataFetcher;
import nl.shootingclub.clubmanager.configuration.datafetcher.user.UserReservationsDataFetcher;
import nl.shootingclub.clubmanager.configuration.datafetcher.userassociation.UserAssociationAssociationRoleDataFetcher;
import nl.shootingclub.clubmanager.configuration.datafetcher.userassociation.UserAssociationMemberSinceDataFetcher;
import nl.shootingclub.clubmanager.configuration.datafetcher.userassociation.UserAssociationUserDataFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import static nl.shootingclub.clubmanager.configuration.datafetcher.user.UserInvitesDataFetcher.userInvitesDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.user.UserLanguageDataFetcher.userLanguageDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.user.UserPresencesDataFetcher.userPresencesDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.user.UserRoleDataFetcher.userRoleDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.userassociation.UserAssociationsDataFetcher.userAssociationsDataFetcher;

@Configuration
public class GraphQLConfig {

    private final LocalDateTimeScalarConfiguration localDateTimeScalarConfiguration;

    private final LongScalarConfiguration longScalarConfiguration;
    private final LocalDateScalarConfiguration localDateScalarConfiguration;

    private final UserImageDataFetcher userImageDataFetcher;
    private final UserFullNameDataFetcher userFullNameDataFetcher;
    private final UserAssociationUserDataFetcher userAssociationUserDataFetcher;
    private final UserAssociationMemberSinceDataFetcher userAssociationMemberSinceDataFetcher;
    private final UserAssociationAssociationRoleDataFetcher userAssociationAssociationRoleDataFetcher;
    private final AssociationInvitesDataFetcher associationInvitesDataFetcher;
    private final AssociationCompetitionsDataFetcher associationCompetitionsDataFetcher;
    private final AssociationContactEmailDataFetcher associationContactEmailDataFetcher;
    private final AssociationActiveDataFetcher associationActiveDataFetcher;
    private final AssociationUsersDataFetcher associationUsersDataFetcher;
    private final AssociationWelcomeMessageDataFetcher associationWelcomeMessageDataFetcher;
    private final UserReservationsDataFetcher userReservationsDataFetcher;
    private final AssociationReservationUsersDataFetcher associationReservationUsersDataFetcher;

    public GraphQLConfig(LocalDateTimeScalarConfiguration localDateTimeScalarConfiguration, LongScalarConfiguration longScalarConfiguration, LocalDateScalarConfiguration localDateScalarConfiguration, UserImageDataFetcher userImageDataFetcher, UserFullNameDataFetcher userFullNameDataFetcher, UserAssociationUserDataFetcher userAssociationUserDataFetcher, UserAssociationMemberSinceDataFetcher userAssociationMemberSinceDataFetcher, UserAssociationAssociationRoleDataFetcher userAssociationAssociationRoleDataFetcher, AssociationInvitesDataFetcher associationInvitesDataFetcher, AssociationCompetitionsDataFetcher associationCompetitionsDataFetcher, AssociationContactEmailDataFetcher associationContactEmailDataFetcher, AssociationActiveDataFetcher associationActiveDataFetcher, AssociationUsersDataFetcher associationUsersDataFetcher, AssociationWelcomeMessageDataFetcher associationWelcomeMessageDataFetcher, UserReservationsDataFetcher userReservationsDataFetcher, AssociationReservationUsersDataFetcher associationReservationUsersDataFetcher) {
        this.localDateTimeScalarConfiguration = localDateTimeScalarConfiguration;
        this.longScalarConfiguration = longScalarConfiguration;
        this.localDateScalarConfiguration = localDateScalarConfiguration;
        this.userImageDataFetcher = userImageDataFetcher;
        this.userFullNameDataFetcher = userFullNameDataFetcher;
        this.userAssociationUserDataFetcher = userAssociationUserDataFetcher;
        this.userAssociationMemberSinceDataFetcher = userAssociationMemberSinceDataFetcher;
        this.userAssociationAssociationRoleDataFetcher = userAssociationAssociationRoleDataFetcher;
        this.associationInvitesDataFetcher = associationInvitesDataFetcher;
        this.associationCompetitionsDataFetcher = associationCompetitionsDataFetcher;
        this.associationContactEmailDataFetcher = associationContactEmailDataFetcher;
        this.associationActiveDataFetcher = associationActiveDataFetcher;
        this.associationUsersDataFetcher = associationUsersDataFetcher;
        this.associationWelcomeMessageDataFetcher = associationWelcomeMessageDataFetcher;
        this.userReservationsDataFetcher = userReservationsDataFetcher;
        this.associationReservationUsersDataFetcher = associationReservationUsersDataFetcher;
    }

    /**
     * Configures the runtime wiring for GraphQL.
     *
     * @return The configured RuntimeWiringConfigurer.
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(localDateTimeScalarConfiguration.localDateTimeScalar())
                .scalar(longScalarConfiguration.longScalar())
                .scalar(localDateScalarConfiguration.localDateScalar())
                .type(TypeRuntimeWiring.newTypeWiring("User")
                        .dataFetcher("language", userLanguageDataFetcher())
                        .dataFetcher("invites", userInvitesDataFetcher())
                        .dataFetcher("role", userRoleDataFetcher())
                        .dataFetcher("reservations", userReservationsDataFetcher)
                        .dataFetcher("presences", userPresencesDataFetcher())
                        .dataFetcher("associations", userAssociationsDataFetcher())
                        .dataFetcher("fullName", userFullNameDataFetcher)
                        .dataFetcher("image", userImageDataFetcher)
                )
                .type(TypeRuntimeWiring.newTypeWiring("UserAssociation")
                        .dataFetcher("user", userAssociationUserDataFetcher)
                        .dataFetcher("memberSince", userAssociationMemberSinceDataFetcher)
                        .dataFetcher("associationRole", userAssociationAssociationRoleDataFetcher)
                )
                .type(TypeRuntimeWiring.newTypeWiring("Association")
                        .dataFetcher("welcomeMessage", associationWelcomeMessageDataFetcher)
                        .dataFetcher("contactEmail", associationContactEmailDataFetcher)
                        .dataFetcher("active", associationActiveDataFetcher)
                        .dataFetcher("users", associationUsersDataFetcher)
                        .dataFetcher("invites", associationInvitesDataFetcher)
                        .dataFetcher("competitions", associationCompetitionsDataFetcher)
                )
                .type(TypeRuntimeWiring.newTypeWiring("Reservation")
                .dataFetcher("reservationUsers", associationReservationUsersDataFetcher));

    }

}