package nl.shootingclub.clubmanager.configuration.datafetcher.association;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.service.ReservationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class AssociationReservationsDataFetcher implements DataFetcher<Set<Reservation>> {

    private final ReservationService reservationService;

    public AssociationReservationsDataFetcher(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    @Observed
    public Set<Reservation> get(DataFetchingEnvironment environment) throws Exception {
        Association association =environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
                LocalDateTime startDate = environment.getArgument("startDate");
                LocalDateTime endDate = environment.getArgument("endDate");

                if(startDate == null && endDate == null) {
                    return association.getReservations();
                }

                return reservationService.findByAssociationIdAndDateRange(association.getId(), startDate, endDate);
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
