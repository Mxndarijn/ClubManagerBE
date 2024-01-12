package nl.shootingclub.clubmanager.exceptions;

import graphql.GraphQLException;

public class UserNotFoundException extends GraphQLException {
    public UserNotFoundException(String message) {
        super(message);
    }
}