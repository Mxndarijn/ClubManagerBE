package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class TrackDTOFull {

    private String name;

    private String description;

    private Set<AllowedWeaponTypeDTO> allowedWeaponTypes;

    private UUID id;
}

