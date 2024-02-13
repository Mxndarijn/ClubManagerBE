package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Reservation;
import nl.shootingclub.clubmanager.model.UserAssociation;

import java.util.Set;

@Setter
@Getter
public class GetReservationResponseDTO {

    private boolean success;
    private Set<Reservation> reservations;
}
