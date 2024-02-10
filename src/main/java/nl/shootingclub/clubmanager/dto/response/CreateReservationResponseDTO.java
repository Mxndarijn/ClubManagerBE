package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Reservation;
import nl.shootingclub.clubmanager.model.ReservationSeries;

import java.util.List;

@Setter
@Getter
public class CreateReservationResponseDTO {

    private boolean success;
    private String message;
    private List<Reservation> reservations;
    private ReservationSeries reservationSeries;
}
