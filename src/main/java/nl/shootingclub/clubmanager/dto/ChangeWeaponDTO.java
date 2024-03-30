package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeWeaponDTO {
    private String weaponName;
    private UUID weaponType;
    private String weaponStatus;
    private UUID weaponID;

}
