package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssociationRepository extends JpaRepository<Association, UUID> {

    @Query("SELECT a FROM Association a JOIN a.users ua WHERE ua.user = :user")
    List<Association> findByUser(User user);

    @Query("SELECT a FROM Association a JOIN a.users ua WHERE ua.user.id = :userID AND a.id = :associationID")
    Optional<Association> findByUserIDAndAssociationID(UUID userID, UUID associationID);

    @Query("SELECT a FROM Association a " +
            "JOIN a.users ua1 ON ua1.user.id = :user1Id " +
            "JOIN a.users ua2 ON ua2.user.id = :user2Id")
    List<Association> findSharedAssociations(UUID user1Id, UUID user2Id);

}
