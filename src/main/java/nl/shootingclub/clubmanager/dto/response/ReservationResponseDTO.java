package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.reservation.Reservation;

@Setter
@Getter
public class ReservationResponseDTO {

    private boolean success;
    private Reservation reservation;

    @Override
    public String toString() {
        return "ReservationResponseDTO{" +
                "success=" + success +
                ", reservation=" + reservation +
                '}';
    }
}
