
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.dto.AssociationInviteDTO;
import nl.shootingclub.clubmanager.exceptions.AssociationNotFoundException;
import nl.shootingclub.clubmanager.exceptions.AssociationRoleNotFoundException;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.*;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
    public Association getAssociationDetails(UUID associationID) {
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
            });
        } else {
            association.setUsers(null);
        }
        //TODO security filters

        return association;
    }

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(dto.associationUUID, T(nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData).MANAGE_MEMBERS)")
    public AssociationInvite sendAssociationInvite(AssociationInviteDTO dto) {

        Optional<Association> optionalAssociation = associationService.getByID(dto.getAssociationUUID());
        if(optionalAssociation.isEmpty()) {
            throw new AssociationNotFoundException("association-not-found");
        }

        Optional<AssociationRole> optionalAssociationRole = associationRoleRepository.findById(dto.getAssociationRoleUUID());
        if(optionalAssociationRole.isEmpty()) {
            throw new AssociationRoleNotFoundException("association-role-not-found");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByEmailEquals(user.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("user-not-found");
        }


        AssociationInvite associationInvite = new AssociationInvite();
        associationInvite.setAssociation(optionalAssociation.get());
        associationInvite.setAssociationRole(optionalAssociationRole.get());
        associationInvite.setUser(optionalUser.get());

        return associationInviteService.createAssociationInvite(associationInvite);


    }
}
