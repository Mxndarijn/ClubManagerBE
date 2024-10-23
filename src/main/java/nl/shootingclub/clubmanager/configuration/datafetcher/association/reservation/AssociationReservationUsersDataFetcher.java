package nl.shootingclub.clubmanager.configuration.datafetcher.association.reservation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.service.ReservationUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class AssociationReservationUsersDataFetcher implements DataFetcher<Set<ReservationUser>> {

    private final ReservationUserService reservationUserService;

    public AssociationReservationUsersDataFetcher(ReservationUserService reservationUserService) {
        this.reservationUserService = reservationUserService;
    }

    @Override
    @Observed
    public Set<ReservationUser> get(DataFetchingEnvironment environment) throws Exception {
        Reservation reservation =environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
                UUID id = environment.getArgument("id");
                if(id == null) {
                    return reservation.getReservationUsers();
                } else {
                    return reservationUserService.findByIdReservationIdAndIdUserId(reservation.getId(), id)
                        .map(Set::of)
                        .orElseGet(Set::of);
                }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
