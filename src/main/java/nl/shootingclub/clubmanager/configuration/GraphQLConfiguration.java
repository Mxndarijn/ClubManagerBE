package nl.shootingclub.clubmanager.configuration;

import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfiguration {


    @Value("${graphql.maxQueryDepth:10}")
    private int maxQueryDepth;

    @Bean
    public Instrumentation maxQueryDepthInstrumentation() {
        if(maxQueryDepth < 0 || maxQueryDepth > 100)
            maxQueryDepth = 10;
        return new MaxQueryDepthInstrumentation(maxQueryDepth);
    }
}
