package nl.shootingclub.clubmanager.configuration.datafetcher.userassociation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class UserAssociationsDataFetcher implements DataFetcher<Set<UserAssociation>> {

    @Bean
    public static UserAssociationsDataFetcher userAssociationsDataFetcher() {
        return new UserAssociationsDataFetcher();
    }
    @Override
    public Set<UserAssociation> get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getAssociations();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
