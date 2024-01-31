
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.dto.GetWeaponMaintenancesDTO;
import nl.shootingclub.clubmanager.service.WeaponMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Controller
public class WeaponMaintenanceController {


    @Autowired
    private WeaponMaintenanceService weaponMaintenanceService;


    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_WEAPONS)")
    public GetWeaponMaintenancesDTO getWeaponMaintenancesBetween(@Argument UUID associationID, @Argument LocalDateTime startDate, @Argument LocalDateTime endDate) {
        GetWeaponMaintenancesDTO dto = new GetWeaponMaintenancesDTO();
        Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
        if (period.getMonths() > 4) {
            dto.setSuccess(false);
            return dto;
        }

        dto.setSuccess(true);
        dto.setMaintenances(weaponMaintenanceService.getAllMaintenances(associationID, startDate, endDate));


        return dto;

    }

}
