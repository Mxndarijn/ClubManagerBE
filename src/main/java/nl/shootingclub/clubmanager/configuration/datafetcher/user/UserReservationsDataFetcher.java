package nl.shootingclub.clubmanager.configuration.datafetcher.user;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.ReservationUser;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class UserReservationsDataFetcher implements DataFetcher<Set<ReservationUser>> {

    @Bean
    public static UserReservationsDataFetcher userReservationsDataFetcher() {
        return new UserReservationsDataFetcher();
    }
    @Override
    @Observed
    public Set<ReservationUser> get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getReservations();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
