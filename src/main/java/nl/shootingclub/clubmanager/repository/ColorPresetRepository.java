package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.data.ColorPreset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ColorPresetRepository extends JpaRepository<ColorPreset, UUID> {

    Optional<ColorPreset> findByColorName(String name);
}
