package nl.shootingclub.clubmanager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {

    @Autowired
    private LocalDateTimeScalarConfiguration scalar;

    /**
     * Configures the runtime wiring for GraphQL.
     *
     * @return The configured RuntimeWiringConfigurer.
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(scalar.localDateTimeScalar());

    }

}