package nl.shootingclub.clubmanager.repository;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Observed
    Set<Reservation> findAllByAssociationIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(UUID associationID, LocalDateTime startDate, LocalDateTime endDate);
}
