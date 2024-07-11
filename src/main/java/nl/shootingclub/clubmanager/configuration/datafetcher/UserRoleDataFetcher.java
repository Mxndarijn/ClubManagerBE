package nl.shootingclub.clubmanager.configuration.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.AccountRole;
import nl.shootingclub.clubmanager.model.ReservationUser;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public class UserRoleDataFetcher implements DataFetcher<AccountRole> {

    @Bean
    public static UserRoleDataFetcher userRoleDataFetcher() {
        return new UserRoleDataFetcher();
    }
    @Override
    public AccountRole get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getRole();
            }
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
