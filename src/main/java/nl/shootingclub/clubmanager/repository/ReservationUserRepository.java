package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Reservation;
import nl.shootingclub.clubmanager.model.ReservationUser;
import nl.shootingclub.clubmanager.model.ReservationUserId;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ReservationUserRepository extends JpaRepository<ReservationUser, ReservationUserId> {

    Optional<ReservationUser> findReservationUserByReservationAndUser(Reservation reservation, User user);
}
