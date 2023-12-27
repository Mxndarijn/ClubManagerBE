package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "association_user_role")
public class AssociationUserRole {

    @EmbeddedId
    private AssociationUserRoleId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private AssociationRole role;

    @ManyToOne
    @MapsId("associationId")
    @JoinColumn(name = "association_id")
    private Association association;

    // Constructors, getters, and setters
}