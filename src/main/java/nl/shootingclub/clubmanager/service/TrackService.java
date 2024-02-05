package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    public Track createTrack(Track track) {
        return trackRepository.save(track);
    }

    public Optional<Track> getByID(UUID trackUUID) {
        return trackRepository.findById(trackUUID);
    }

    public Track saveTrack(Track track) {
        return trackRepository.save(track);
    }

    public void deleteTrack(Track track) {
        trackRepository.delete(track);
    }
}