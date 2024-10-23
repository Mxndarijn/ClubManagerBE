package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.reservation.Reservation;

@Setter
@Getter
public class GetSpecificReservationResponseDTO {

    private boolean success;
    private Reservation reservation;
}
