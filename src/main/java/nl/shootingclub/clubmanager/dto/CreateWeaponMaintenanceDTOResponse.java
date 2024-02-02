package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.WeaponMaintenance;

@Setter
@Getter
public class CreateWeaponMaintenanceDTOResponse {

    private boolean success;
    private String message;
    private WeaponMaintenance maintenance;
}
