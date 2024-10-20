package nl.shootingclub.clubmanager.configuration.datafetcher.user;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.service.ReservationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class UserReservationsDataFetcher implements DataFetcher<Set<ReservationUser>> {

    @Autowired
    private ReservationUserService reservationUserService;

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

                String startDateArg = environment.getArgument("startDate");
                String endDateArg = environment.getArgument("endDate");

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;
                try {
                    if (startDateArg != null) {
                        startDate = LocalDateTime.parse(startDateArg);
                    }
                } catch (Exception ignored) {
                }
                try {
                    if (endDateArg != null) {
                        endDate = LocalDateTime.parse(endDateArg);
                    }
                } catch (Exception ignored) {
                }

                if(startDate == null && endDate == null) {
                    return user.getReservations();
                }

                return reservationUserService.findByUserIdAndDateRange(user.getId(), startDate, endDate);
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
