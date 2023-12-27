package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.ReservationSeries;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationSeriesRepository extends JpaRepository<ReservationSeries, UUID> {
}
