package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Track;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class WeaponTypeDTO {

    private UUID id;
    private String name;
    private Set<Track> tracks;
}