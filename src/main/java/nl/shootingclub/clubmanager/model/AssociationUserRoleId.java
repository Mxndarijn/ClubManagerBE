package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class AssociationUserRoleId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "association_id")
    private UUID associationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssociationUserRoleId that = (AssociationUserRoleId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(associationId, that.associationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId, associationId);
    }

}