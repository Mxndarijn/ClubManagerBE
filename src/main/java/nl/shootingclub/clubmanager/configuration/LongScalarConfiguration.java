package nl.shootingclub.clubmanager.configuration;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.language.IntValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LongScalarConfiguration {

    @Bean
    public GraphQLScalarType longScalar() {
        return GraphQLScalarType.newScalar()
                .name("Long")
                .description("Custom Scalar for handling Java Longs")
                .coercing(new Coercing<Long, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof Long || dataFetcherResult instanceof Integer) {
                            return dataFetcherResult.toString();
                        }
                        if(dataFetcherResult instanceof Duration) {
                             return ((Duration) dataFetcherResult).toNanos() + "";
                        }
                        throw new CoercingSerializeException("Expected a Long object.");
                    }

                    @Override
                    public Long parseValue(Object input) throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return Long.parseLong((String) input);
                            } else if (input instanceof Integer) {
                                return ((Integer) input).longValue();
                            } else if (input instanceof Long) {
                                return (Long) input;
                            }
                            throw new CoercingParseValueException("Invalid input for Long: " + input + ", expected type String or Long.");
                        } catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Unable to parse input as Long: " + input, e);
                        }
                    }

                    @Override
                    public Long parseLiteral(Object input) throws CoercingParseLiteralException {
                        if (input instanceof IntValue) {
                            return ((IntValue) input).getValue().longValueExact();
                        }
                        throw new CoercingParseLiteralException("Input must be an IntValue for Long type.");
                    }
                }).build();
    }
}
