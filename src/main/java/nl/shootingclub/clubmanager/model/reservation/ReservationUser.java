package nl.shootingclub.clubmanager.model.reservation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reservation_user")
public class ReservationUser {

    @EmbeddedId
    private ReservationUserId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("reservationId")
    @JoinColumn(name = "reservation_id", nullable = false)

    private Reservation reservation;

    @Column(nullable = false)
    private LocalDateTime registerDate;

    @Column(name = "user_number")
    private Integer position;

}
