package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AllowedWeaponTypeDTO {
    private UUID id;
    private String name;
}
