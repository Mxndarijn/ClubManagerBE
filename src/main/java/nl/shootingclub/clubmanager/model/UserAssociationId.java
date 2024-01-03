package nl.shootingclub.clubmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class UserAssociationId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "association_id")
    private UUID associationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAssociationId that = (UserAssociationId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(associationId, that.associationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, associationId);
    }

}

