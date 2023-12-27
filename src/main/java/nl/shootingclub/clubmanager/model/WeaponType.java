package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "weapon_type")
public class WeaponType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(length = 255, unique = true)
    private String name;

    @ManyToMany(mappedBy = "allowedWeaponTypes")
    private Set<Track> tracks;
}
