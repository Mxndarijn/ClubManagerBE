
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.data.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.data.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
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

    /**
     * Creates a new Association with default values and performs necessary operations to associate it with a User.
     *
     * @return The created Association.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.data.AccountPermissionData).CREATE_ASSOCIATION)")
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

    /**
     * Retrieves the details of an association.
     *
     * @param associationID The ID of the association to retrieve.
     * @return The Association object containing the details of the association.
     *         Returns null if the user does not have permission to manage the association or if the association is not found.
     * @throws UserNotFoundException If the authenticated user is not found in the user repository.
     */
    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_MEMBERS)")
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
        //TODO security filters

        return association;
    }

    /**
     * Retrieves the statistics of an association.
     *
     * @param associationID The ID of the association.
     * @return The AssociationStatisticsDTO object containing the total number of members, weapons, and tracks of the association,
     *          or null if the association does not exist.
     */
    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_MEMBERS)")
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

    /**
     * Updates the profile picture of an association.
     *
     * @param dto           The DTO containing the new profile picture details.
     * @param associationID The ID of the association to update.
     * @return The response DTO indicating the success of the operation or any error message.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_SETTINGS)")
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

    /**
     * Updates the settings of an Association.
     *
     * @param dto The data transfer object containing the updated values for the Association.
     * @param associationID The ID of the Association to update.
     * @return The response object indicating the success of the update operation.
     */
    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_SETTINGS)")
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
