package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssociationInviteRepository extends JpaRepository<AssociationInvite, UUID> {

    List<AssociationInvite> findByUser(User user);
}
