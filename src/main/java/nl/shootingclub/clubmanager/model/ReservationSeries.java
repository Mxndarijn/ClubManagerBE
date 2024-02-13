package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.data.ReservationRepeat;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reservation_series")
public class ReservationSeries {

    public ReservationSeries() {
        reservations = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "reservationSerie")
    private Set<Reservation> reservations;


    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "maxUsers")
    private int maxUsers;

    @ManyToOne
    @JoinColumn(name = "association_id", referencedColumnName = "id")
    private Association association;



}