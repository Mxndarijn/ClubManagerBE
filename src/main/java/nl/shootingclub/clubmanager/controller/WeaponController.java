
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.helper.ImageHelper;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

@Controller
public class WeaponController {

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


    /**
     * Creates a new weapon for the given association.
     *
     * @param dto The CreateWeaponDTO containing the details of the weapon to be created.
     * @param associationID The ID of the association.
     * @return The CreateWeaponResponseDTO containing the success status, message, and the created weapon.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_WEAPONS)")
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

    /**
     * Retrieves all weapons associated with a given association.
     *
     * @param associationID the ID of the association
     * @return a Set containing all weapons associated with the given association
     */
    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).VIEW_WEAPONS)")
    public Set<Weapon> getAllWeapons(@Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            return new HashSet<>();
        }

        Association association = optionalAssociation.get();

        return association.getWeapons();

    }

    /**
     * Retrieves all weapon types associated with the given association ID.
     *
     * @param associationID The ID of the association.
     * @return A list of WeaponType objects.
     */
    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).VIEW_WEAPONS)")
    public List<WeaponType> getAllWeaponTypes(@Argument UUID associationID) {
        return weaponTypeRepository.findAll();
    }

}
