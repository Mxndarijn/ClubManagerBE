package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.reservation.ReservationSeries;
import nl.shootingclub.clubmanager.repository.ReservationSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationSeriesService {

    @Autowired
    private ReservationSeriesRepository reservationSeriesRepository;

    public ReservationSeries createReservationSeries(ReservationSeries reservationSeries) {
        return reservationSeriesRepository.save(reservationSeries);
    }

    public Optional<ReservationSeries> getByID(UUID reservationUUID) {
        return reservationSeriesRepository.findById(reservationUUID);
    }

    public ReservationSeries saveReservationSeries(ReservationSeries reservationSeries) {
        return reservationSeriesRepository.save(reservationSeries);
    }

    public void deleteReservationSeries(ReservationSeries reservationSeries) {
        reservationSeriesRepository.delete(reservationSeries);
    }
}