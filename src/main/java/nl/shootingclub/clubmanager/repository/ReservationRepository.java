package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Reservation;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Set<Reservation> findAllByAssociationIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(UUID associationID, LocalDateTime startDate, LocalDateTime endDate);
}
