package nl.shootingclub.clubmanager.model;
import jakarta.persistence.*;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "user_association")
@Entity
public class UserAssociation {

    @EmbeddedId
    private UserAssociationId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("associationId")
    @JoinColumn(name = "association_id")
    private Association association;

    @Column(name = "member_since")
    private LocalDateTime memberSince;


    @ManyToOne
    @JoinColumn(name = "association_role_id")
    private AssociationRole associationRole;

    public UserAssociation() {
    }

    public void createID() {
        if(id != null)
            return;
        UserAssociationId id = new UserAssociationId();
        id.setAssociationId(association.getId());
        id.setUserId(user.getId());
        setId(id);
    }

}



