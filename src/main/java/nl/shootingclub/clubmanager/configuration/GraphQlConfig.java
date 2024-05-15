package nl.shootingclub.clubmanager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {

    private final LocalDateTimeScalarConfiguration localDateTimeScalarConfiguration;

    private final LongScalarConfiguration longScalarConfiguration;

    public GraphQlConfig(LocalDateTimeScalarConfiguration localDateTimeScalarConfiguration, LongScalarConfiguration longScalarConfiguration) {
        this.localDateTimeScalarConfiguration = localDateTimeScalarConfiguration;
        this.longScalarConfiguration = longScalarConfiguration;
    }

    /**
     * Configures the runtime wiring for GraphQL.
     *
     * @return The configured RuntimeWiringConfigurer.
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(localDateTimeScalarConfiguration.localDateTimeScalar())
                .scalar(longScalarConfiguration.longScalar());

    }

}