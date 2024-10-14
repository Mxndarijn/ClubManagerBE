package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.data.DefaultImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DefaultImageRepository extends JpaRepository<DefaultImage, UUID> {
    Optional<DefaultImage> findByName(String name);
}
