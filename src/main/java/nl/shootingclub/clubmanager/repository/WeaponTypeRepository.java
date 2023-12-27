package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.WeaponType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeaponTypeRepository extends JpaRepository<WeaponType, UUID> {
}
