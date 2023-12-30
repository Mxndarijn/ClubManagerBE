package nl.shootingclub.clubmanager.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccountBadCredentialsException extends AuthenticationException {
    public AccountBadCredentialsException(String message) {
        super(message);
    }
}