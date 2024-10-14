package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.reservation.Reservation;

import java.util.Set;

@Setter
@Getter
public class GetReservationResponseDTO {

    private boolean success;
    private Set<Reservation> reservations;
}
