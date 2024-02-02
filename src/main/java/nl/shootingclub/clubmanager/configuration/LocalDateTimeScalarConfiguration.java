package nl.shootingclub.clubmanager.configuration;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class LocalDateTimeScalarConfiguration {

    @Bean
    public GraphQLScalarType localDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Java +++ LocalDateTime as an ISO String")
                .coercing(new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(@NotNull Object dataFetcherResult, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } else {
                            throw new CoercingSerializeException("Expected a LocalDateTime object.");
                        }
                    }

                    @Override
                    public LocalDateTime parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } else {
                            throw new CoercingParseLiteralException("Expected a ISO_LOCAL_DATE_TIME string.");
                        }
                    }

                    @Override
                    public LocalDateTime parseValue(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseValueException {
                        if (input instanceof String) {
                            try {
                                return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        } else {
                            throw new CoercingParseValueException("Expected a ISO_LOCAL_DATE_TIME string.");
                        }
                    }

                    @Override
                    public @NotNull Value<?> valueToLiteral(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) {
                        if (input instanceof LocalDateTime) {
                            return new StringValue(((LocalDateTime) input).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } else {
                            throw new IllegalArgumentException("Expected a LocalDateTime object.");
                        }
                    }

                }).build();
    }
}