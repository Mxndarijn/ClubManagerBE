package nl.shootingclub.clubmanager.exceptions;

import graphql.GraphQLException;

public class AssociationRoleNotFoundException extends GraphQLException {
    public AssociationRoleNotFoundException(String message) {
        super(message);
    }
}