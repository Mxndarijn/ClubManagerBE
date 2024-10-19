package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationSeries;

import java.util.Set;

@Setter
@Getter
public class CreateReservationResponseDTO {

    private boolean success;
    private String message;
    private Set<Reservation> reservations;
    private ReservationSeries reservationSeries;

    @Override
    public String toString() {
        return "CreateReservationResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", reservations=" + reservations +
                ", reservationSeries=" + reservationSeries +
                '}';
    }
}
