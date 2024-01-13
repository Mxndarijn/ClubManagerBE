
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @QueryMapping
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData).GET_MY_PROFILE)")
    public User getMyProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUser(user);
        if(optionalUser.isPresent()) {
            User u = optionalUser.get();
            u.getAssociations().forEach(a -> {
                a.getAssociation().setUsers(null);
            });
            return u;
        }
        return null;
    }
}
