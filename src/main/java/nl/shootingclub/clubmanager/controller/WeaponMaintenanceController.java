
package nl.shootingclub.clubmanager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.ColorPreset;
import nl.shootingclub.clubmanager.model.Weapon;
import nl.shootingclub.clubmanager.model.WeaponMaintenance;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

@Controller
public class WeaponMaintenanceController {


    @Autowired
    private WeaponMaintenanceService weaponMaintenanceService;

    @Autowired
    private AssociationService associationService;

    @Autowired
    private DefaultImageRepository defaultImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AssociationRoleRepository associationRoleRepository;

    @Autowired
    private UserAssociationService userAssociationService;

    @Autowired
    private AssociationInviteService associationInviteService;

    @Autowired
    private UserAssociationRepository userAssociationRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private WeaponTypeRepository weaponTypeRepository;

    @Autowired
    private WeaponService weaponService;

    @Autowired
    private ColorPresetService colorPresetService;


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

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_WEAPONS)")
    public CreateWeaponMaintenanceDTOResponse createWeaponMaintenance(@Argument CreateWeaponMaintenanceDTO dto) {
        CreateWeaponMaintenanceDTOResponse response = new CreateWeaponMaintenanceDTOResponse();
        if(dto.getStartDate().isAfter(dto.getEndDate())) {
            response.setSuccess(false);
            response.setMessage("start-is-after-end");
            return response;
        }
        WeaponMaintenance maintenance = new WeaponMaintenance();

        Optional<Association> optionalAssociation = associationService.getByID(dto.getAssociationUUID());
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }
        Optional<Weapon> optionalWeapon = weaponService.getByID(dto.getWeaponUUID());
        if (optionalWeapon.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("weapon-not-found");
            return response;
        }
        Optional<ColorPreset> optionalColorPreset = colorPresetService.getByID(dto.getColorPresetUUID());
        if (optionalColorPreset.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("color-preset-not-found");
            return response;
        }

        maintenance.setWeapon(optionalWeapon.get());
        maintenance.setStartDate(dto.getStartDate());
        maintenance.setEndDate(dto.getEndDate());
        maintenance.setColorPreset(optionalColorPreset.get());
        maintenance.setAssociation(optionalAssociation.get());
        maintenance.setTitle(dto.getTitle());
        maintenance.setDescription(dto.getDescription());

        maintenance = weaponMaintenanceService.createWeaponMaintenance(maintenance);

        response.setMaintenance(maintenance);
        response.setSuccess(true);
        response.setMessage("done");


        return response;


    }

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_WEAPONS)")
    public CreateWeaponMaintenanceDTOResponse changeWeaponMaintenance(@Argument ChangeWeaponMaintenanceDTO dto) {
        CreateWeaponMaintenanceDTOResponse response = new CreateWeaponMaintenanceDTOResponse();
        if(dto.getStartDate().isAfter(dto.getEndDate())) {
            response.setSuccess(false);
            response.setMessage("start-is-after-end");
            return response;
        }


        Optional<Association> optionalAssociation = associationService.getByID(dto.getAssociationUUID());
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }
        Optional<Weapon> optionalWeapon = weaponService.getByID(dto.getWeaponUUID());
        if (optionalWeapon.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("weapon-not-found");
            return response;
        }
        Optional<ColorPreset> optionalColorPreset = colorPresetService.getByID(dto.getColorPresetUUID());
        if (optionalColorPreset.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("color-preset-not-found");
            return response;
        }

        Optional<WeaponMaintenance> optionalWeaponMaintenance = weaponMaintenanceService.getById(dto.getWeaponMaintenanceUUID());
        if (optionalWeaponMaintenance.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("weapon-maintenance-not-found");
            return response;
        }

        WeaponMaintenance maintenance = optionalWeaponMaintenance.get();

        maintenance.setWeapon(optionalWeapon.get());
        maintenance.setStartDate(dto.getStartDate());
        maintenance.setEndDate(dto.getEndDate());
        maintenance.setColorPreset(optionalColorPreset.get());
        maintenance.setAssociation(optionalAssociation.get());
        maintenance.setTitle(dto.getTitle());
        maintenance.setDescription(dto.getDescription());

        maintenance = weaponMaintenanceService.createWeaponMaintenance(maintenance);

        response.setMaintenance(maintenance);
        response.setSuccess(true);
        response.setMessage("done");


        return response;


    }

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_WEAPONS)")
    public DefaultBooleanResponseDTO deleteWeaponMaintenance(@Argument UUID maintenanceID, @Argument UUID associationID) {
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        Optional<WeaponMaintenance> optionalWeaponMaintenance = weaponMaintenanceService.getById(maintenanceID);

        if (optionalWeaponMaintenance.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("weapon-maintenance-not-found");
            return response;
        }

        WeaponMaintenance maintenance = optionalWeaponMaintenance.get();

        weaponMaintenanceService.deleteMaintenance(maintenance);

        response.setSuccess(true);
        response.setMessage("done");


        return response;


    }

}
