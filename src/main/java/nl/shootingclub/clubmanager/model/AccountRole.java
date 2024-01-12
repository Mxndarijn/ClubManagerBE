package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account_role")
public class AccountRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<User> users;

    @ManyToMany
    @JoinTable()
    private Set<AccountPermission> permissions;


    @Column(name = "canBeDeleted", nullable = false)
    private boolean canBeDeleted = true;

}
