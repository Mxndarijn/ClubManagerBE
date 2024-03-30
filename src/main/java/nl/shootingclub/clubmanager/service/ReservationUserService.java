package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.Reservation;
import nl.shootingclub.clubmanager.model.ReservationUser;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.ReservationRepository;
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

    public Optional<ReservationUser> findReservationUserByReservationAndUser(Reservation reservation, User user) {
        return reservationRepository.findReservationUserByReservationAndUser(reservation, user);
    }
}