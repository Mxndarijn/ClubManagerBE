package nl.shootingclub.clubmanager.model.reservation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.model.data.ColorPreset;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {

    public Reservation() {
        this.reservationUsers = new HashMap<>();
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

    @Column()
    private boolean membersCanChooseTheirOwnPosition;

//    @Column
//    @Enumerated(EnumType.STRING)
//    private ReservationStatus status;

    @Column(name = "max_size")
    private Integer maxSize;

    @OneToMany(mappedBy = "reservation")
    @MapKeyColumn(name = "user_number")
    private Map<Integer, ReservationUser> reservationUsers;

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

