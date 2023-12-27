package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "association_id", referencedColumnName = "id")
    private Association association;

    @Column
    private LocalDateTime date;

    @Column()
    private String title;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "max_size")
    private Integer maxSize;

    @ManyToMany
    @JoinTable()
    private Set<User> users;

    @ManyToOne
    @JoinColumn(name = "reservation_series_id", referencedColumnName = "id")
    private ReservationSeries reservationSerie;

}

