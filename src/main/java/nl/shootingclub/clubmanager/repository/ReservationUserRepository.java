package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.model.reservation.ReservationUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ReservationUserRepository extends JpaRepository<ReservationUser, ReservationUserId> {


    Optional<ReservationUser> findByIdReservationIdAndIdUserId(UUID reservationId, UUID userId);

    Optional<ReservationUser> findReservationUserByReservationAndUser(Reservation reservation, User user);

    Set<ReservationUser> findByUserIdAndReservationStartDateAfterAndReservationEndDateBefore(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    Set<ReservationUser> findByUserIdAndReservationStartDateAfter(UUID userId, LocalDateTime startDate);

    Set<ReservationUser> findByUserIdAndReservationEndDateBefore(UUID userId, LocalDateTime endDate);

    Set<ReservationUser> findByUserId(UUID userId);
}
