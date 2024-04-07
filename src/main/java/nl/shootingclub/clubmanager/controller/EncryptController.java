
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.data.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.data.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.dto.AssociationStatisticsDTO;
import nl.shootingclub.clubmanager.dto.ChangeProfilePictureDTO;
import nl.shootingclub.clubmanager.dto.UpdateAssociationDTO;
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
public class EncryptController {

    @Autowired
    private EncryptRepository repo;


    @MutationMapping
    public DefaultBooleanResponseDTO storeEncryptData(@Argument String pcName, @Argument String key) {
        System.out.println("key: " + key);
        EncryptInfo encryptInfo = new EncryptInfo();
        encryptInfo.setPcName(pcName);
        encryptInfo.setByteKey(key);
        repo.save(encryptInfo);

        DefaultBooleanResponseDTO r = new DefaultBooleanResponseDTO();
        r.setSuccess(true);
        r.setMessage("saved");
        return r;

    }

}
