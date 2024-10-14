 package nl.shootingclub.clubmanager.model;

 import jakarta.persistence.*;
 import lombok.Getter;
 import lombok.Setter;
 import nl.shootingclub.clubmanager.model.account.AccountRole;
 import nl.shootingclub.clubmanager.model.data.Image;
 import nl.shootingclub.clubmanager.model.reservation.ReservationUser;

 import java.time.LocalDateTime;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.UUID;

 @Getter
@Setter
@Entity
@Table(name = "LedenCentraalUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "language")
    private String language;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "knsa_membership_number")
    private Integer knsaMembershipNumber;

    @Column(name = "knsa_member_since")
    private LocalDateTime knsaMemberSince;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @OneToMany(mappedBy = "user")
    private Set<UserAssociation> associations;

    @OneToMany(mappedBy = "user")
    private Set<UserPresence> presences;

    @OneToMany(mappedBy = "user")
    private Set<AssociationInvite> invites;

    @OneToMany(mappedBy = "user")
    private Set<ReservationUser> reservations;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id")
    private AccountRole role;

     public User() {
         this.associations = new HashSet<>();
         this.presences = new HashSet<>();
         this.reservations = new HashSet<>();
     }

 }

