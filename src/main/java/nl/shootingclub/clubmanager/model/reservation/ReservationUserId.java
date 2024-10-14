package nl.shootingclub.clubmanager.model.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@Builder
public class ReservationUserId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "competition_id")
    private UUID reservationId;

    public ReservationUserId() {

    }

    public ReservationUserId(UUID userId, UUID reservationId) {
        this.userId = userId;
        this.reservationId = reservationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationUserId that = (ReservationUserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, reservationId);
    }
}
