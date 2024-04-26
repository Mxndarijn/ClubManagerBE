
package nl.shootingclub.clubmanager.resolver.association;

import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.dto.AssociationInviteDTO;
import nl.shootingclub.clubmanager.dto.ChangeUserAssociationDTO;
import nl.shootingclub.clubmanager.dto.DeleteUserAssociationDTO;
import nl.shootingclub.clubmanager.dto.InputAssociationInviteDTO;
import nl.shootingclub.clubmanager.dto.response.ChangeUserAssociationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.dto.response.GetReservationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.SendAssociationInviteResponseDTO;
import nl.shootingclub.clubmanager.exceptions.AssociationNotFoundException;
import nl.shootingclub.clubmanager.exceptions.AssociationRoleNotFoundException;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AssociationRoleRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AssociationMemberResolver {

    private final AssociationInviteService associationInviteService;
    private final UserAssociationService userAssociationService;
    private final PermissionService permissionService;
    private final AssociationService associationService;
    private final UserRepository userRepository;
    private final UserAssociationRepository userAssociationRepository;
    private final AssociationRoleRepository associationRoleRepository;

    public AssociationMemberResolver(AssociationInviteService associationInviteService, UserAssociationService userAssociationService, PermissionService permissionService, AssociationService associationService, UserRepository userRepository, UserAssociationRepository userAssociationRepository, AssociationRoleRepository associationRoleRepository) {
        this.associationInviteService = associationInviteService;
        this.userAssociationService = userAssociationService;
        this.permissionService = permissionService;
        this.associationService = associationService;
        this.userRepository = userRepository;
        this.userAssociationRepository = userAssociationRepository;
        this.associationRoleRepository = associationRoleRepository;
    }


//    @SchemaMapping(typeName = "AssociationQueries")
//    public AssociationMemberResolver associationMemberQueries() {
//        return this;
//    }

    @SchemaMapping(typeName = "AssociationMutations")
    public AssociationMemberResolver associationMemberMutations() {
        return this;
    }


    /**
     * Sends an association invite to a user.
     *
     * @param dto The AssociationInviteDTO containing the necessary details for the association invitation.
     *               - userEmail: The email address of the user to invite to the association. (String)
     *               - associationUUID: The UUID of the association. (UUID)
     *               - associationRoleUUID: The UUID of the association role. (UUID)
     * @return SendAssociationInviteResponseDTO The response DTO containing the result of the invitation.
     *               - success: True if the invitation was sent successfully, false otherwise. (boolean)
     *               - message: A message describing the outcome of the invitation. (String)
     *               - associationInvite: The created AssociationInvite object if the invitation was sent successfully, null otherwise.
     */
    @SchemaMapping(typeName = "AssociationMemberMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_MEMBERS)")
    public SendAssociationInviteResponseDTO sendAssociationInvite(@Argument AssociationInviteDTO dto) {

        Optional<Association> optionalAssociation = associationService.getByID(dto.getAssociationUUID());
        if(optionalAssociation.isEmpty()) {
            throw new AssociationNotFoundException("association-not-found");
        }

        Optional<AssociationRole> optionalAssociationRole = associationRoleRepository.findById(dto.getAssociationRoleUUID());
        if(optionalAssociationRole.isEmpty()) {
            throw new AssociationRoleNotFoundException("association-role-not-found");
        }
        SendAssociationInviteResponseDTO responseDTO = new SendAssociationInviteResponseDTO();
        Optional<User> optionalUser = userRepository.findByEmailEquals(dto.getUserEmail());
        if (optionalUser.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("user-not-found");
            return responseDTO;
        }

        Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(optionalUser.get().getId(), dto.getAssociationUUID());
        if(optionalUserAssociation.isPresent()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("already-in-association");
            return responseDTO;
        }


        Optional<AssociationInvite> optionalAssociationInvite = associationInviteService.findAssociationInviteByUserIDAndAssociationID(optionalUser.get(), optionalAssociation.get());
        if(optionalAssociationInvite.isPresent()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("already-invited");
        } else {
            responseDTO.setSuccess(true);
            AssociationInvite invite = new AssociationInvite();

            AssociationInviteId id = new AssociationInviteId();
            id.setAssociationId(optionalAssociation.get().getId());
            id.setUserId(optionalUser.get().getId());

            invite.setId(id);
            invite.setAssociation(optionalAssociation.get());
            invite.setAssociationRole(optionalAssociationRole.get());
            invite.setUser(optionalUser.get());
            invite.setCreatedAt(LocalDateTime.now());

            responseDTO.setAssociationInvite(associationInviteService.saveAssociationInvite(invite));
        }

        return responseDTO;




    }

    /**
     * Removes an association invite.
     *
     * @param inviteId The input invitation ID object {@link InputAssociationInviteDTO} that contains the user UUID and association UUID.
     * @return A {@link DefaultBooleanResponseDTO} object indicating the success or failure of the removal operation.
     */
    @SchemaMapping(typeName = "AssociationMemberMutations")
    public DefaultBooleanResponseDTO removeAssociationInvite(@Argument InputAssociationInviteDTO inviteId) {

        AssociationInviteId id = new AssociationInviteId();
        id.setAssociationId(inviteId.getAssociationUUID());
        id.setUserId(inviteId.getUserUUID());

        Optional<AssociationInvite> optionalAssociationInvite = associationInviteService.findAssociationInviteByID(id);
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        if(optionalAssociationInvite.isPresent()) {
            AssociationInvite invite = optionalAssociationInvite.get();
            if(permissionService.validateAssociationPermission(invite.getAssociation().getId(), AssociationPermissionData.MANAGE_MEMBERS)) {
                associationInviteService.deleteAssociationInvite(invite);
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                response.setMessage("no-permission");
            }

        } else {
            response.setSuccess(false);
            response.setMessage("no-invite-found");
        }

        return response;
    }

    /**
     * Accepts an association invite and creates a user association.
     *
     * @param inviteId The input association invite ID containing the user UUID and association UUID.
     * @return A DefaultBooleanResponseDTO indicating the success or failure of accepting the invitation.
     */
    @SchemaMapping(typeName = "AssociationMemberMutations")
    public DefaultBooleanResponseDTO acceptAssociationInvite(@Argument InputAssociationInviteDTO inviteId) {

        AssociationInviteId id = new AssociationInviteId();
        id.setAssociationId(inviteId.getAssociationUUID());
        id.setUserId(inviteId.getUserUUID());

        Optional<AssociationInvite> optionalAssociationInvite = associationInviteService.findAssociationInviteByID(id);
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        if(optionalAssociationInvite.isPresent()) {
            AssociationInvite invite = optionalAssociationInvite.get();
            UserAssociation userAssociation = userAssociationService.createUserAssociation(invite.getUser(), invite.getAssociation(), invite.getAssociationRole());
            associationInviteService.removeAssociationInvite(invite);
            if(userAssociation != null) {
                response.setSuccess(true);
            }

        } else {
            response.setSuccess(false);
            response.setMessage("no-invite-found");
        }

        return response;
    }

    /**
     * Rejects an association invite.
     *
     * @param inviteId The ID of the association invite to reject.
     * @return A DefaultBooleanResponseDTO indicating the success of the operation and an optional message.
     */
    @SchemaMapping(typeName = "AssociationMemberMutations")
    public DefaultBooleanResponseDTO rejectAssociationInvite(@Argument InputAssociationInviteDTO inviteId) {

        AssociationInviteId id = new AssociationInviteId();
        id.setAssociationId(inviteId.getAssociationUUID());
        id.setUserId(inviteId.getUserUUID());

        Optional<AssociationInvite> optionalAssociationInvite = associationInviteService.findAssociationInviteByID(id);
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        if(optionalAssociationInvite.isPresent()) {
            AssociationInvite invite = optionalAssociationInvite.get();
            associationInviteService.removeAssociationInvite(invite);
            response.setSuccess(true);

        } else {
            response.setSuccess(false);
            response.setMessage("no-invite-found");
        }

        return response;
    }

    /**
     * Changes the association of a user.
     *
     * @param changeUserAssociationDTO The DTO containing the necessary information for changing the user association.
     *                                Required fields: userUUID, associationUUID, associationRoleUUID.
     * @return The response DTO indicating the success of the operation and the updated UserAssociation object.
     *         If the user association does not exist, returns null.
     * @throws AssociationRoleNotFoundException If the specified association role UUID is not found.
     */
    @SchemaMapping(typeName = "AssociationMemberMutations")
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
    @SchemaMapping(typeName = "AssociationMemberMutations")
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
