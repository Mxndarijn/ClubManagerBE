
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Controller
public class AssociationController {

    @Autowired
    private AssociationService associationService;

    @QueryMapping
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData).DEFAULT_GET_MY_ASSOCIATIONS)")
    public List<Association> getMyAssociations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Association> list = associationService.getMyAssociations(user);
        list.forEach(a -> {
            a.setAssociationUserRoles(null);
            a.setUsers(null);
        });
        return list;
    }
}
