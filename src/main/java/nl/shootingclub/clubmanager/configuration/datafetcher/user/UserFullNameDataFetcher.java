package nl.shootingclub.clubmanager.configuration.datafetcher.user;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.helper.UserHelper;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserFullNameDataFetcher implements DataFetcher<String> {
    private final UserHelper userHelper;

    public UserFullNameDataFetcher(UserHelper userHelper) {
        this.userHelper = userHelper;
    }

    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        User user =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getFullName();
            }
            if(userHelper.doUsersHaveSharedAssociation(user, contextUser)) {
                return user.getFullName();
            }

        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
