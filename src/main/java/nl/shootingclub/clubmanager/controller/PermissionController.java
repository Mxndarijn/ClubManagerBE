
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.dto.AssociationPermissionDTO;
import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.service.PermissionService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @QueryMapping
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData).GET_MY_PERMISSIONS)")
    public List<AccountPermission> getMyPermissions() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUserById(user.getId());
        if(optionalUser.isEmpty()) {
            return new ArrayList<>();
        }
        List<AccountPermission> list = permissionService.getMyPermissions(optionalUser.get());
        list.forEach(a -> {
            a.setRoles(null);
        });
        return list;
    }

    @QueryMapping
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData).GET_MY_PERMISSIONS)")
    public List<AssociationPermissionDTO> getMyAssociationPermissions() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUserById(user.getId());
        if(optionalUser.isEmpty()) {
            return new ArrayList<>();
        }
        Set<UserAssociation> list = userService.getUserById(optionalUser.get().getId()).get().getAssociations();
        return list.stream().map(AssociationPermissionDTO::new).collect(Collectors.toList());
    }
}
