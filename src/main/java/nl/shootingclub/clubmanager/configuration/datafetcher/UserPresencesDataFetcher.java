package nl.shootingclub.clubmanager.configuration.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.model.UserPresence;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class UserPresencesDataFetcher implements DataFetcher<Set<UserPresence>> {

    @Bean
    public static UserPresencesDataFetcher userPresencesDataFetcher() {
        return new UserPresencesDataFetcher();
    }
    @Override
    public Set<UserPresence> get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getPresences();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
