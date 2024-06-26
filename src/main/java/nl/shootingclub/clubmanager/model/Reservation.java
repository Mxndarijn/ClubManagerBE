package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {

    public Reservation() {
        this.reservationUsers = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "association_id", referencedColumnName = "id")
    private Association association;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column()
    private String title;

    @Lob
    private String description;

//    @Column
//    @Enumerated(EnumType.STRING)
//    private ReservationStatus status;

    @Column(name = "max_size")
    private Integer maxSize;

    @OneToMany(mappedBy = "reservation")
    private Set<ReservationUser> reservationUsers;

    @ManyToMany
    @JoinTable()
    private Set<Track> tracks;

    @ManyToMany
    @JoinTable()
    private Set<WeaponType> allowedWeaponTypes;

    @ManyToOne
    @JoinColumn(name = "reservation_series_id", referencedColumnName = "id")
    private ReservationSeries reservationSeries;

    @ManyToOne
    @JoinColumn(name = "color_preset_id", referencedColumnName = "id")
    private ColorPreset colorPreset;



}

