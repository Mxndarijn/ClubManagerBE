package nl.shootingclub.clubmanager.exceptions;

import graphql.GraphQLException;

public class AssociationNotFoundException extends GraphQLException {
    public AssociationNotFoundException(String message) {
        super(message);
    }
}