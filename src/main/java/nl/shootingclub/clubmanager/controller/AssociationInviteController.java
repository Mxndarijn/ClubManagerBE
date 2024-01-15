
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.dto.AssociationInviteDTO;
import nl.shootingclub.clubmanager.dto.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.dto.InputAssociationInviteDTO;
import nl.shootingclub.clubmanager.dto.SendAssociationInviteResponseDTO;
import nl.shootingclub.clubmanager.exceptions.AssociationNotFoundException;
import nl.shootingclub.clubmanager.exceptions.AssociationRoleNotFoundException;
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
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AssociationInviteController {

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


    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_MEMBERS)")
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

    @MutationMapping
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

    @MutationMapping
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

    @MutationMapping
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
}
