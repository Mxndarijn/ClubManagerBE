package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssociationRepository extends JpaRepository<Association, UUID> {
}
