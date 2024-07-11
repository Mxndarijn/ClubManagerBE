package nl.shootingclub.clubmanager.configuration.datafetcher.user;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserLanguageDataFetcher implements DataFetcher<String> {

    @Bean
    public static UserLanguageDataFetcher userLanguageDataFetcher() {
        return new UserLanguageDataFetcher();
    }
    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getLanguage();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
