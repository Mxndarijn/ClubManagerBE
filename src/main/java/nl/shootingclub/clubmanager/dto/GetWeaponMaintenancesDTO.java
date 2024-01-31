package nl.shootingclub.clubmanager.dto;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.model.WeaponMaintenance;

import java.util.List;

@Setter
@Getter
public class GetWeaponMaintenancesDTO {

    private boolean success;
    private List<WeaponMaintenance> maintenances;
}
