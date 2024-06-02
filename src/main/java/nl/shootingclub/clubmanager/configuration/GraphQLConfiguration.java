package nl.shootingclub.clubmanager.configuration;

import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.accept;

@Configuration
public class GraphQLConfiguration {


    @Value("${graphql.maxQueryDepth:10}")
    private int maxQueryDepth;

    /**
     * Creates and returns an Instrumentation object for enforcing a maximum query depth in GraphQL queries.
     * If the 'graphql.maxQueryDepth' property is not set or its value is outside the range [0, 100],
     * the default value of 10 will be used.
     *
     * @return the Instrumentation object for enforcing a maximum query depth
     */
    @Bean
    public Instrumentation maxQueryDepthInstrumentation() {
        if(maxQueryDepth < 0 || maxQueryDepth > 100)
            maxQueryDepth = 10;
        return new MaxQueryDepthInstrumentation(maxQueryDepth);
    }

}
