 package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

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

    @ManyToMany(mappedBy = "users")
    private Set<Reservation> reservations;

    @ManyToMany
    @JoinTable()
    private Set<AccountRole> roles;

    @OneToMany(mappedBy = "user")
    private Set<AssociationUserRole> associationUserRoles;

}

