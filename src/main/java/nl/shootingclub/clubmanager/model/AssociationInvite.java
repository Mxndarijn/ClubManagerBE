package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "association_invite")
public class AssociationInvite {

    @EmbeddedId
    private AssociationInviteId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("associationId")
    @JoinColumn(name = "association_id")
    private Association association;


    @ManyToOne
    @JoinColumn(name = "association_role_id")
    private AssociationRole associationRole;




}