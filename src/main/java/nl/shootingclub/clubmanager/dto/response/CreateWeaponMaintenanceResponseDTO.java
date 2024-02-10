package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.WeaponMaintenance;

@Setter
@Getter
public class CreateWeaponMaintenanceResponseDTO {

    private boolean success;
    private String message;
    private WeaponMaintenance maintenance;
}
