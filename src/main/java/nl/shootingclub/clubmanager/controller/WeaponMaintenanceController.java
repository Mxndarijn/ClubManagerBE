
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


    /**
     * Retrieves a list of weapon maintenances within a specified date range for a given association.
     *
     * @param associationID The UUID of the association.
     * @param startDate The start date of the date range.
     * @param endDate The end date of the date range.
     * @return An instance of GetWeaponMaintenancesDTO containing the list of maintenances and a success flag. If the period between the start date and end date is greater than 4
     * months, the success flag will be set to false and the list of maintenances will be empty.
     */
    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
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

    /**
     * Create a new weapon maintenance.
     *
     * @param dto The CreateWeaponMaintenanceDTO containing the necessary information to create the maintenance.
     * @return The CreateWeaponMaintenanceDTOResponse indicating the success or failure of the operation.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
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

    /**
     * Updates the maintenance details of a weapon.
     *
     * @param dto The DTO containing the updated maintenance information.
     * @return The response containing the updated maintenance details or an error message.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
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

    /**
     * Delete a weapon maintenance.
     *
     * @param maintenanceID   The ID of the maintenance to delete.
     * @param associationID   The ID of the association.
     * @return The response indicating whether the deletion was successful.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
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
