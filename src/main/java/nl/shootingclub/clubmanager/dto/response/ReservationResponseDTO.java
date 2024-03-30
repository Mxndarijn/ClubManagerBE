package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Reservation;

import java.util.Set;

@Setter
@Getter
public class ReservationResponseDTO {

    private boolean success;
    private Reservation reservation;
}
