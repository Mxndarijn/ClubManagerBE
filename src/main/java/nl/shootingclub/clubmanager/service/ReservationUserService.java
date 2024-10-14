package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.repository.ReservationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationUserService {

    @Autowired
    private ReservationUserRepository reservationRepository;

    @Autowired
    private ReservationUserRepository reservationUserRepository;

    public Optional<ReservationUser> findReservationUserByReservationAndUser(Reservation reservation, User user) {
        return reservationRepository.findReservationUserByReservationAndUser(reservation, user);
    }

    public ReservationUser saveReservationUser(ReservationUser reservationUser) {
        return reservationUserRepository.save(reservationUser);
    }
}