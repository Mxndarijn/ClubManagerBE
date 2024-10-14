package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.model.reservation.ReservationUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationUserRepository extends JpaRepository<ReservationUser, ReservationUserId> {

    Optional<ReservationUser> findReservationUserByReservationAndUser(Reservation reservation, User user);
}
