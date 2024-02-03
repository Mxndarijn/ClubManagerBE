
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.dto.AssociationInviteDTO;
import nl.shootingclub.clubmanager.exceptions.AssociationNotFoundException;
import nl.shootingclub.clubmanager.exceptions.AssociationRoleNotFoundException;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AssociationRoleRepository;
import nl.shootingclub.clubmanager.repository.DefaultImageRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PermissionController {

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
     * Retrieves a list of all association roles.
     *
     * @return a list of AssociationRole objects representing all association roles.
     */
    @QueryMapping
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData).GET_ASSOCIATION_ROLES)")
    public List<AssociationRole> getAssociationRoles() {
        return associationRoleRepository.findAll();
    }

}
