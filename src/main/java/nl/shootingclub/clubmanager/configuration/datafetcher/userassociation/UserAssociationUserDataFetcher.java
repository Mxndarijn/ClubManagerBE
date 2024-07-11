package nl.shootingclub.clubmanager.configuration.datafetcher.userassociation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.helper.UserHelper;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.service.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAssociationUserDataFetcher implements DataFetcher<User> {
    private final UserAssociationRepository userAssociationRepository;

    public UserAssociationUserDataFetcher(UserAssociationRepository userAssociationRepository) {
        this.userAssociationRepository = userAssociationRepository;
    }

    @Override
    public User get(DataFetchingEnvironment environment) throws Exception {
        UserAssociation userAssociation =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(userAssociation.getUser().getId())) {
                return userAssociation.getUser();
            }
            List<UserAssociation> contextUserUserAssociationList = userAssociationRepository.findByUserId(contextUser.getId());
            for (UserAssociation userAssociation1 : contextUserUserAssociationList) {
                if (userAssociation1.getAssociation().getId().equals(userAssociation.getAssociation().getId())) {
                    return userAssociation.getUser();
                }
            }

        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
