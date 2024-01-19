package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateWeaponDTO {
    private String weaponName;
    private UUID weaponType;
    private String weaponStatus;

}
