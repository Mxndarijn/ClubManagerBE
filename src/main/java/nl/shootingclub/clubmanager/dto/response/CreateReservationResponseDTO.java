package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Reservation;
import nl.shootingclub.clubmanager.model.ReservationSeries;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class CreateReservationResponseDTO {

    private boolean success;
    private String message;
    private Set<Reservation> reservations;
    private ReservationSeries reservationSeries;
}
