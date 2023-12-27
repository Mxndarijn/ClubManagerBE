package nl.shootingclub.clubmanager.resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import nl.shootingclub.clubmanager.dto.RegisterInput;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class RegisterResolver implements GraphQLMutationResolver {
    public User register(RegisterInput input) {
        // Voer de registratielogica uit en retourneer de geregistreerde gebruiker
        User newUser = new User();
        return newUser;
    }

    public Boolean test() {

        return true;
    }
}
