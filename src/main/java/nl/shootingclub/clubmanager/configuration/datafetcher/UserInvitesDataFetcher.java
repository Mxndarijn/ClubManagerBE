package nl.shootingclub.clubmanager.configuration.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.AccountRole;
import nl.shootingclub.clubmanager.model.AssociationInvite;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class UserInvitesDataFetcher implements DataFetcher<Set<AssociationInvite>> {

    @Bean
    public static UserInvitesDataFetcher userInvitesDataFetcher() {
        return new UserInvitesDataFetcher();
    }
    @Override
    public Set<AssociationInvite> get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getInvites();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
