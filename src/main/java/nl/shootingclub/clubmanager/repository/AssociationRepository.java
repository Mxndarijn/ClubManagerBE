package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AssociationRepository extends JpaRepository<Association, UUID> {

    @Query("SELECT a FROM Association a JOIN a.users ua WHERE ua.user = :user")
    List<Association> findByUser(User user);

}
