package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "association_permission")
public class AssociationPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    @Column(nullable = false)
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<AssociationRole> roles;

}
