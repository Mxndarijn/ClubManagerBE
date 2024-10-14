
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.data.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.data.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.model.data.Image;
import nl.shootingclub.clubmanager.repository.AssociationRoleRepository;
import nl.shootingclub.clubmanager.repository.DefaultImageRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.UserAssociationService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

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
            userAssociation.setMemberSince(LocalDateTime.now());

            associationRoleRepository.findByName(DefaultRoleAssociation.ADMIN.getName()).ifPresent(userAssociation::setAssociationRole);
            userAssociation.createID();
            userAssociationService.createUserAssociation(userAssociation);

            u.getAssociations().add(userAssociation);

            userService.saveUser(u);
        });

        return a;
    }

}
