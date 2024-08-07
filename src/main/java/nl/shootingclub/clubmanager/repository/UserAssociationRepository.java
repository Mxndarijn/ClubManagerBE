package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.model.UserAssociationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAssociationRepository extends JpaRepository<UserAssociation, UserAssociationId> {

    Optional<UserAssociation> findByUserIdAndAssociationId(UUID userId, UUID associationId);

    List<UserAssociation> findByUserId(UUID id);
}