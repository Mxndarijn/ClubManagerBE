package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account_role")
public class AssociationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true)
    private String name;

    @ManyToMany
    @JoinTable()
    private Set<AssociationPermission> permissions;

    @OneToMany(mappedBy = "role")
    private Set<AssociationUserRole> associationUserRoles;

}
