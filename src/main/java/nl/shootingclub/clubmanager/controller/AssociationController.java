
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.exceptions.AssociationNotFoundException;
import nl.shootingclub.clubmanager.exceptions.AssociationRoleNotFoundException;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.helper.ImageHelper;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AssociationController {

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
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData).CREATE_ASSOCIATION)")
    public Association createAssociation() {
        Association association = new Association();
        association.setContactEmail("ContactEmail-Placeholder");
        association.setActive(true);

        defaultImageRepository.findByName(DefaultImageData.ASSOCIATION_PICTURE.getName()).ifPresent(image -> {
            Image i = new Image();
            i.setEncoded(image.getImage().getEncoded());
            association.setImage(i);
        });
        association.setName("AssociationName-Placeholder");
        association.setWelcomeMessage("WelcomeMessage-Placeholder");

        Association a = associationService.createAssociation(association);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.getUser(user).ifPresent(u-> {
            UserAssociation userAssociation = new UserAssociation();
            userAssociation.setAssociation(a);
            userAssociation.setUser(u);

            associationRoleRepository.findByName(DefaultRoleAssociation.ADMIN.getName()).ifPresent(userAssociation::setAssociationRole);
            userAssociation.createID();
            userAssociationService.createUserAssociation(userAssociation);

            u.getAssociations().add(userAssociation);

            userService.saveUser(u);
        });

        return a;
    }

    @QueryMapping
    public Association getAssociationDetails(@Argument UUID associationID) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByEmailEquals(user.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("user-not-found");
        }
        User u = optionalUser.get();
        Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(u.getId(), associationID);
        if(optionalUserAssociation.isEmpty())
            return null;
        UserAssociation userAssociation = optionalUserAssociation.get();
        Association association = userAssociation.getAssociation();
        if(permissionService.validateAssociationPermission(associationID, AssociationPermissionData.MANAGE_MEMBERS)) {
            association.getUsers().forEach(ui -> {
                ui.getUser().setAssociations(null);
                ui.getUser().setPresences(null);
                association.getInvites().forEach(invite -> {
                    invite.getUser().setAssociations(null);
                    invite.getUser().setImage(null);
                    invite.getUser().setRole(null);
                    invite.getUser().setPresences(null);
                    invite.getUser().setKnsaMembershipNumber(null);
                    invite.getUser().setKnsaMembershipNumber(null);
                });
            });
        } else {
            association.setInvites(null);
            association.setUsers(null);
        }
        //TODO security filters

        return association;
    }

    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_MEMBERS)")
    public AssociationStatisticsDTO getAssociationStatistics(@Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty())
            return null;

        Association association = optionalAssociation.get();

        AssociationStatisticsDTO statisticsDTO = new AssociationStatisticsDTO();
        statisticsDTO.setTotalMembers(association.getUsers().size());
        statisticsDTO.setTotalTracks(association.getTracks().size());
        statisticsDTO.setTotalWeapons(association.getWeapons().size());

        return statisticsDTO;
    }

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_SETTINGS)")
    public DefaultBooleanResponseDTO updateAssociationPicture(@Argument ChangeProfilePictureDTO dto, @Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-association-found");
            return responseDTO;
        }

        Association association = optionalAssociation.get();
        Image i = association.getImage();

        try {
            i.setEncoded(ImageHelper.scaleImage(dto.getImage(), 720));
            association.setImage(i);
        } catch (IOException e) {
            System.out.println("error");
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("could-not-convert");
            return responseDTO;
        }

        associationService.saveAssociation(association);
        DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setMessage("changed");

        return responseDTO;


    }

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_SETTINGS)")
    public DefaultBooleanResponseDTO updateAssociationSettings(@Argument UpdateAssociationDTO dto, @Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
        if(optionalAssociation.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-association-found");
            return responseDTO;
        }

        Association association = optionalAssociation.get();
        association.setName(dto.getAssociationName());
        association.setWelcomeMessage(dto.getWelcomeMessage());
        association.setContactEmail(dto.getContactEmail());

        associationService.saveAssociation(association);


        responseDTO.setSuccess(true);
        responseDTO.setMessage("changed");
        return responseDTO;


    }

}
