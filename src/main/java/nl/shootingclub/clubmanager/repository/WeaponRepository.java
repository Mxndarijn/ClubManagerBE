package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeaponRepository extends JpaRepository<Weapon, UUID> {
}
