package nl.shootingclub.clubmanager.configuration.datafetcher.userassociation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.model.AssociationRole;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.service.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserAssociationAssociationRoleDataFetcher implements DataFetcher<AssociationRole> {
    private final PermissionService permissionService;

    public UserAssociationAssociationRoleDataFetcher(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public AssociationRole get(DataFetchingEnvironment environment) throws Exception {
        UserAssociation userAssociation =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(userAssociation.getUser().getId())) {
                return userAssociation.getAssociationRole();
            }
            if(permissionService.validateAssociationPermission(userAssociation.getAssociation().getId(), AssociationPermissionData.MANAGE_MEMBERS)) {
                return userAssociation.getAssociationRole();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
