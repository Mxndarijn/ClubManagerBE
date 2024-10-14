package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.reservation.ReservationSeries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationSeriesRepository extends JpaRepository<ReservationSeries, UUID> {
}
