package nl.shootingclub.clubmanager.configuration.datafetcher.userassociation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.helper.UserHelper;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.service.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserAssociationMemberSinceDataFetcher implements DataFetcher<LocalDateTime> {
    private final PermissionService permissionService;

    public UserAssociationMemberSinceDataFetcher(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public LocalDateTime get(DataFetchingEnvironment environment) throws Exception {
        UserAssociation userAssociation =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(userAssociation.getUser().getId())) {
                return userAssociation.getMemberSince();
            }
            if(permissionService.validateAssociationPermission(userAssociation.getAssociation().getId(), AssociationPermissionData.MANAGE_MEMBERS)) {
                return userAssociation.getMemberSince();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
