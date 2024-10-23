package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.repository.ReservationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ReservationUserService {

    @Autowired
    private ReservationUserRepository reservationRepository;

    @Autowired
    private ReservationUserRepository reservationUserRepository;

    public Optional<ReservationUser> findReservationUserByReservationAndUser(Reservation reservation, User user) {
        return reservationRepository.findReservationUserByReservationAndUser(reservation, user);
    }

    public Optional<ReservationUser> findByIdReservationIdAndIdUserId(UUID reservationID, UUID userID) {
        return reservationRepository.findByIdReservationIdAndIdUserId(reservationID, userID);
    }

    public ReservationUser saveReservationUser(ReservationUser reservationUser) {
        return reservationUserRepository.save(reservationUser);
    }

    /**
     * Haal ReservationUser records op voor een specifieke gebruiker en optioneel binnen een tijdspanne.
     *
     * @param userId    ID van de gebruiker
     * @param startDate Optionele startdatum voor filtering
     * @param endDate   Optionele einddatum voor filtering
     * @return Set van gefilterde ReservationUser records
     */
    public Set<ReservationUser> findByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return reservationUserRepository.findByUserIdAndReservationStartDateAfterAndReservationEndDateBefore(userId, startDate, endDate);
        } else if (startDate != null) {
            return reservationUserRepository.findByUserIdAndReservationStartDateAfter(userId, startDate);
        } else if (endDate != null) {
            return reservationUserRepository.findByUserIdAndReservationEndDateBefore(userId, endDate);
        } else {
            return reservationUserRepository.findByUserId(userId);
        }
    }
}