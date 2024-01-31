package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.ColorPreset;
import nl.shootingclub.clubmanager.model.WeaponType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ColorPresetRepository extends JpaRepository<ColorPreset, UUID> {

    Optional<ColorPreset> findByName(String name);
}
