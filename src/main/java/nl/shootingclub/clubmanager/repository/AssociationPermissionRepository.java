package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AssociationPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AssociationPermissionRepository extends JpaRepository<AssociationPermission, UUID> {
    Optional<AssociationPermission> findByName(String name);

    @Query("SELECT CASE WHEN COUNT(ua) > 0 THEN true ELSE false END " +
            "FROM UserAssociation ua " +
            "JOIN ua.associationRole ar " +
            "JOIN ar.permissions p " +
            "WHERE ua.user.id = :userId " +
            "AND ua.association.id = :associationUUID " +
            "AND p.name = :permissionName")
    boolean hasPermissionForAssociation(UUID userId, UUID associationUUID, String permissionName);

}
