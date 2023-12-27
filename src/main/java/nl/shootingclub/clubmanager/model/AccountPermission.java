package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account_permission")
public class AccountPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(length = 255, unique = true)
    private String name;

    @Lob
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<AccountRole> roles;

}