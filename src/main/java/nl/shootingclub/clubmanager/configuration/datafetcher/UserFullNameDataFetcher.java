package nl.shootingclub.clubmanager.configuration.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserFullNameDataFetcher implements DataFetcher<String> {

    @Bean
    public static UserFullNameDataFetcher userFullNameDataFetcher() {
        return new UserFullNameDataFetcher();
    }
    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getFullName();
            }
            boolean hasSharedAssociation = contextUser.getAssociations().stream()
                    .map(UserAssociation::getAssociation)
                    .anyMatch(association -> user.getAssociations().stream()
                            .map(UserAssociation::getAssociation)
                            .anyMatch(userAssociation -> userAssociation.getId().equals(association.getId()))
                    );
            if(hasSharedAssociation) {
                return user.getFullName();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
