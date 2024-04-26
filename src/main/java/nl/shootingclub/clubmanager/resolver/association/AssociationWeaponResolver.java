
package nl.shootingclub.clubmanager.resolver.association;

import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.dto.response.CreateWeaponMaintenanceResponseDTO;
import nl.shootingclub.clubmanager.dto.response.CreateWeaponResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.WeaponTypeRepository;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.ColorPresetService;
import nl.shootingclub.clubmanager.service.WeaponMaintenanceService;
import nl.shootingclub.clubmanager.service.WeaponService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
public class AssociationWeaponResolver {

    private final AssociationService associationService;
    private final WeaponMaintenanceService weaponMaintenanceService;
    private final WeaponTypeRepository weaponTypeRepository;
    private final WeaponService weaponService;
    private final ColorPresetService colorPresetService;

    public AssociationWeaponResolver(AssociationService associationService, WeaponMaintenanceService weaponMaintenanceService, WeaponTypeRepository weaponTypeRepository, WeaponService weaponService, ColorPresetService colorPresetService) {
        this.associationService = associationService;
        this.weaponMaintenanceService = weaponMaintenanceService;
        this.weaponTypeRepository = weaponTypeRepository;
        this.weaponService = weaponService;
        this.colorPresetService = colorPresetService;
    }

    @SchemaMapping(typeName = "AssociationQueries")
    public AssociationWeaponResolver associationWeaponQueries() {
        return this;
    }
    @SchemaMapping(typeName = "AssociationMutations")
    public AssociationWeaponResolver associationWeaponMutations() {
        return this;
    }

    /**
     * Retrieves all weapons associated with a given association.
     *
     * @param associationID the ID of the association
     * @return a Set containing all weapons associated with the given association
     */
    @SchemaMapping(typeName = "AssociationWeaponQueries")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_WEAPONS)")
    public Set<Weapon> getAllWeapons(@Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            return new HashSet<>();
        }

        Association association = optionalAssociation.get();

        return association.getWeapons();

    }

    /**
     * Retrieves a list of weapon maintenances within a specified date range for a given association.
     *
     * @param associationID The UUID of the association.
     * @param startDate The start date of the date range.
     * @param endDate The end date of the date range.
     * @return An instance of GetWeaponMaintenancesDTO containing the list of maintenances and a success flag. If the period between the start date and end date is greater than 4
     * months, the success flag will be set to false and the list of maintenances will be empty.
     */
    @SchemaMapping(typeName = "AssociationWeaponQueries")
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
     * Creates a new weapon for the given association.
     *
     * @param dto The CreateWeaponDTO containing the details of the weapon to be created.
     * @param associationID The ID of the association.
     * @return The CreateWeaponResponseDTO containing the success status, message, and the created weapon.
     */
    @SchemaMapping(typeName = "AssociationWeaponMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
    public CreateWeaponResponseDTO createWeapon(@Argument CreateWeaponDTO dto, @Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        CreateWeaponResponseDTO responseDTO = new CreateWeaponResponseDTO();
        if(optionalAssociation.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-association-found");
            return responseDTO;
        }

        Association association = optionalAssociation.get();

        Optional<WeaponType> optionalWeaponType = weaponTypeRepository.findById(dto.getWeaponType());
        if(optionalWeaponType.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-weapon-type-found");
            return responseDTO;
        }
        WeaponStatus weaponStatus;
        try {
            weaponStatus = WeaponStatus.valueOf(dto.getWeaponStatus());
        } catch (IllegalArgumentException e) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("invalid-weapon-status");
            return responseDTO;
        }


        Weapon weapon = new Weapon();
        weapon.setAssociation(association);
        weapon.setName(dto.getWeaponName());
        weapon.setType(optionalWeaponType.get());
        weapon.setStatus(weaponStatus);

        weapon = weaponService.saveWeapon(weapon);

        responseDTO.setSuccess(true);
        responseDTO.setWeapon(weapon);
        responseDTO.setMessage("created");
        return responseDTO;
    }

    @SchemaMapping(typeName = "AssociationWeaponMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
    public CreateWeaponResponseDTO changeWeapon(@Argument ChangeWeaponDTO dto, @Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        CreateWeaponResponseDTO responseDTO = new CreateWeaponResponseDTO();
        if(optionalAssociation.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-association-found");
            return responseDTO;
        }

        Optional<WeaponType> optionalWeaponType = weaponTypeRepository.findById(dto.getWeaponType());
        if(optionalWeaponType.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-weapon-type-found");
            return responseDTO;
        }
        WeaponStatus weaponStatus;
        try {
            weaponStatus = WeaponStatus.valueOf(dto.getWeaponStatus());
        } catch (IllegalArgumentException e) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("invalid-weapon-status");
            return responseDTO;
        }

        Optional<Weapon> optionalWeapon = weaponService.getByID(dto.getWeaponID());
        if(optionalWeapon.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-weapon-found");
            return responseDTO;
        }
        Weapon weapon = optionalWeapon.get();
        if(!weapon.getAssociation().getId().equals(associationID)) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("weapon-association-no-match");
            return responseDTO;
        }

        weapon.setName(dto.getWeaponName());
        weapon.setType(optionalWeaponType.get());
        weapon.setStatus(weaponStatus);

        weapon = weaponService.saveWeapon(weapon);

        responseDTO.setSuccess(true);
        responseDTO.setWeapon(weapon);
        responseDTO.setMessage("changed");
        return responseDTO;
    }

    /**
     * Create a new weapon maintenance.
     *
     * @param dto The CreateWeaponMaintenanceDTO containing the necessary information to create the maintenance.
     * @return The CreateWeaponMaintenanceDTOResponse indicating the success or failure of the operation.
     */
    @SchemaMapping(typeName = "AssociationWeaponMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
    public CreateWeaponMaintenanceResponseDTO createWeaponMaintenance(@Argument CreateWeaponMaintenanceDTO dto) {
        CreateWeaponMaintenanceResponseDTO response = new CreateWeaponMaintenanceResponseDTO();
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
    @SchemaMapping(typeName = "AssociationWeaponMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_WEAPONS)")
    public CreateWeaponMaintenanceResponseDTO changeWeaponMaintenance(@Argument ChangeWeaponMaintenanceDTO dto) {
        CreateWeaponMaintenanceResponseDTO response = new CreateWeaponMaintenanceResponseDTO();
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
    @SchemaMapping(typeName = "AssociationWeaponMutations")
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
