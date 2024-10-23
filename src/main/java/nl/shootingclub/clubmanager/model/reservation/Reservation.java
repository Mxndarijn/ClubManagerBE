package nl.shootingclub.clubmanager.model.reservation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.model.data.ColorPreset;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reservation",
        indexes = {
            @Index(name = "idx_reservation_association_start_end", columnList = "association_id, startDate, endDate")
        })
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

    @Column()
    private boolean membersCanChooseTheirOwnPosition;

//    @Column
//    @Enumerated(EnumType.STRING)
//    private ReservationStatus status;

    @Column(name = "max_size")
    private Integer maxSize;

    @OneToMany(mappedBy = "reservation")
    @MapKeyColumn(name = "user_number")
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

    @ElementCollection
    @CollectionTable(name = "reservation_open_positions", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "position")
    private Set<Integer> openPositions = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void initializeOpenPositions() {
        if (maxSize != null && openPositions.isEmpty()) {
            for (int i = 0; i < maxSize; i++) {
                openPositions.add(i);
            }
        }
    }


}

