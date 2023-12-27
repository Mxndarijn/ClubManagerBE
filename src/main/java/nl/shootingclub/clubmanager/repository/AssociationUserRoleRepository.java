package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.AssociationUserRole;
import nl.shootingclub.clubmanager.model.AssociationUserRoleId;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssociationUserRoleRepository extends JpaRepository<AssociationUserRole, AssociationUserRoleId> {
}
