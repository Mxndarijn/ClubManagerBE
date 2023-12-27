package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {

}
