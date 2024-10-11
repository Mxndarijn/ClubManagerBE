package nl.shootingclub.clubmanager.configuration;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class LocalDateScalarConfiguration {

    /**
     * Creates a GraphQLScalarType representing a LocalDateTime as an ISO String.
     *
     * @return The created GraphQLScalarType.
     */
    @Bean
    public GraphQLScalarType localDateScalar() {
        return GraphQLScalarType.newScalar()
                .name("LocalDate")
                .description("Java LocalDate as an ISO String")
                .coercing(new Coercing<LocalDate, String>() {
                    @Override
                    public String serialize(@NotNull Object dataFetcherResult, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDate) {
                            return ((LocalDate) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE);
                        } else {
                            throw new CoercingSerializeException("Expected a LocalDate object.");
                        }
                    }

                    @Override
                    public LocalDate parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            return LocalDate.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                        } else {
                            throw new CoercingParseLiteralException("Expected an ISO_LOCAL_DATE string.");
                        }
                    }

                    @Override
                    public LocalDate parseValue(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseValueException {
                        if (input instanceof String) {
                            try {
                                return LocalDate.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        } else {
                            throw new CoercingParseValueException("Expected an ISO_LOCAL_DATE string.");
                        }
                    }

                    @Override
                    public @NotNull Value<?> valueToLiteral(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) {
                        if (input instanceof LocalDate) {
                            return new StringValue(((LocalDate) input).format(DateTimeFormatter.ISO_LOCAL_DATE));
                        } else {
                            throw new IllegalArgumentException("Expected a LocalDate object.");
                        }
                    }

                }).build();
    }
}