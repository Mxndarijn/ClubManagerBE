package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;

@Getter
@Setter
@Entity
@Table(name = "association")
public class Association {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @Lob
    @Column(name = "welcome_message")
    private String welcomeMessage;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column()
    private boolean active;

    @OneToMany(mappedBy = "association")
    private Set<UserAssociation> users;

    @OneToMany(mappedBy = "association")
    private Set<AssociationInvite> invites;

    @OneToMany(mappedBy = "association")
    private Set<Track> tracks;

    @OneToMany(mappedBy = "association")
    private Set<Weapon> weapons;


}
