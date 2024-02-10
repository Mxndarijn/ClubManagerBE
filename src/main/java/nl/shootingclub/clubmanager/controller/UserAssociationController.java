
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.dto.response.ChangeUserAssociationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.exceptions.AssociationRoleNotFoundException;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AssociationRoleRepository;
import nl.shootingclub.clubmanager.repository.DefaultImageRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class UserAssociationController {

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

    /**
     * Changes the association of a user.
     *
     * @param changeUserAssociationDTO The DTO containing the necessary information for changing the user association.
     *                                Required fields: userUUID, associationUUID, associationRoleUUID.
     * @return The response DTO indicating the success of the operation and the updated UserAssociation object.
     *         If the user association does not exist, returns null.
     * @throws AssociationRoleNotFoundException If the specified association role UUID is not found.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#changeUserAssociationDTO.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_MEMBERS)")
    public ChangeUserAssociationResponseDTO changeUserAssociation(@Argument ChangeUserAssociationDTO changeUserAssociationDTO) {
        Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(changeUserAssociationDTO.getUserUUID(), changeUserAssociationDTO.getAssociationUUID());
        if(optionalUserAssociation.isEmpty())
            return null;
        UserAssociation userAssociation = optionalUserAssociation.get();


        Optional<AssociationRole> optionalAssociationRole = associationRoleRepository.findById(changeUserAssociationDTO.getAssociationRoleUUID());
        if(optionalAssociationRole.isEmpty()) {
            throw new AssociationRoleNotFoundException("association-role-not-found");
        }
        userAssociation.setAssociationRole(optionalAssociationRole.get());

        userAssociationService.saveUserAssociation(userAssociation);
        ChangeUserAssociationResponseDTO response = new ChangeUserAssociationResponseDTO();
        response.setSuccess(true);
        response.setUserAssociation(userAssociation);

        return response;
    }

    /**
     * Removes the association between a user and an association.
     *
     * @param deleteUserAssociationDTO The DTO containing the information to remove the user association:
     *                                - userUUID: The UUID of the user
     *                                - associationUUID: The UUID of the association
     * @return The response indicating the success of removing the user association.
     *         - success: True if the user association is removed successfully, false otherwise.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#deleteUserAssociationDTO.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_MEMBERS)")
    public DefaultBooleanResponseDTO removeUserAssociation(@Argument DeleteUserAssociationDTO deleteUserAssociationDTO) {
        Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(deleteUserAssociationDTO.getUserUUID(), deleteUserAssociationDTO.getAssociationUUID());
        if(optionalUserAssociation.isEmpty())
            return null;
        UserAssociation userAssociation = optionalUserAssociation.get();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByEmailEquals(user.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("user-not-found");
        }
        User u = optionalUser.get();

        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        if(!userAssociation.getUser().getId().equals(u.getId())) {
            userAssociationService.deleteUserAssociation(userAssociation);
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
        }


        return response;
    }

}
