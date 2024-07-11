package nl.shootingclub.clubmanager.configuration;

import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import static nl.shootingclub.clubmanager.configuration.datafetcher.UserAssociationsDataFetcher.userAssociationsDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserFullNameDataFetcher.userFullNameDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserImageDataFetcher.userImageDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserInvitesDataFetcher.userInvitesDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserLanguageDataFetcher.userLanguageDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserPresencesDataFetcher.userPresencesDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserReservationsDataFetcher.userReservationsDataFetcher;
import static nl.shootingclub.clubmanager.configuration.datafetcher.UserRoleDataFetcher.userRoleDataFetcher;

@Configuration
public class GraphQLConfig {

    private final LocalDateTimeScalarConfiguration localDateTimeScalarConfiguration;

    private final LongScalarConfiguration longScalarConfiguration;

    public GraphQLConfig(LocalDateTimeScalarConfiguration localDateTimeScalarConfiguration, LongScalarConfiguration longScalarConfiguration) {
        this.localDateTimeScalarConfiguration = localDateTimeScalarConfiguration;
        this.longScalarConfiguration = longScalarConfiguration;
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
                .type(TypeRuntimeWiring.newTypeWiring("User")
                        .dataFetcher("language", userLanguageDataFetcher())
                        .dataFetcher("invites", userInvitesDataFetcher())
                        .dataFetcher("role", userRoleDataFetcher())
                        .dataFetcher("reservations", userReservationsDataFetcher())
                        .dataFetcher("presences", userPresencesDataFetcher())
                        .dataFetcher("associations", userAssociationsDataFetcher())
                        .dataFetcher("fullName", userFullNameDataFetcher())
                        .dataFetcher("image", userImageDataFetcher())
                );

    }

}