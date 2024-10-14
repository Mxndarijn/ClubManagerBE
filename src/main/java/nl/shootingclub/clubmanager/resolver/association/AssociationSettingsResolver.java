package nl.shootingclub.clubmanager.resolver.association;

import nl.shootingclub.clubmanager.dto.ChangeProfilePictureDTO;
import nl.shootingclub.clubmanager.dto.UpdateAssociationDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.helper.ImageHelper;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.data.Image;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AssociationSettingsResolver {

    private final AssociationService associationService;

    public AssociationSettingsResolver(AssociationService associationService) {
        this.associationService = associationService;
    }

//    @SchemaMapping(typeName = "AssociationQueries")
//    public AssociationSettingsResolver associationSettingsQueries() {
//        return this;
//    }

    @SchemaMapping(typeName = "AssociationMutations")
    public AssociationSettingsResolver associationSettingsMutations() {
        return this;
    }

    /**
     * Updates the profile picture of an association.
     *
     * @param dto           The DTO containing the new profile picture details.
     * @param associationID The ID of the association to update.
     * @return The response DTO indicating the success of the operation or any error message.
     */
    @SchemaMapping(typeName = "AssociationSettingsMutations")
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
    @SchemaMapping(typeName = "AssociationSettingsMutations")
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
