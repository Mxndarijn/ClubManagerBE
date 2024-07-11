package nl.shootingclub.clubmanager.configuration.datafetcher.association;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.service.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AssociationInvitesDataFetcher implements DataFetcher<Set<AssociationInvite>> {
    private final PermissionService permissionService;

    public AssociationInvitesDataFetcher(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public Set<AssociationInvite> get(DataFetchingEnvironment environment) throws Exception {
        Association association =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(permissionService.validateAssociationPermission(association.getId(), AssociationPermissionData.MANAGE_MEMBERS))
                return association.getInvites();
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
