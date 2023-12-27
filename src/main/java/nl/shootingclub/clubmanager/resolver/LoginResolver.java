package nl.shootingclub.clubmanager.resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import nl.shootingclub.clubmanager.dto.RegisterInput;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller
public class LoginResolver {

    @QueryMapping
    public Boolean test() {

        return true;
    }

    @QueryMapping
    public String greeting(@Argument String name) {
        return "Hello, " + name + "!";
    }
}
