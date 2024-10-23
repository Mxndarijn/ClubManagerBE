package nl.shootingclub.clubmanager.service;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> getByID(UUID reservationUUID) {
        return reservationRepository.findById(reservationUUID);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Observed
    public Set<Reservation> getAllReservations(UUID associationID, LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findAllByAssociationIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(associationID, startDate, endDate);
    }

    public Set<Reservation> findByAssociationIdAndDateRange(UUID id, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return reservationRepository.findAllByAssociationIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(id, startDate, endDate);
        } else if (startDate != null) {
            return reservationRepository.findByAssociationIdAndStartDateAfter(id, startDate);
        } else if (endDate != null) {
            return reservationRepository.findByAssociationIdAndEndDateBefore(id, endDate);
        } else {
            return reservationRepository.findByAssociationId(id);
        }
    }
}