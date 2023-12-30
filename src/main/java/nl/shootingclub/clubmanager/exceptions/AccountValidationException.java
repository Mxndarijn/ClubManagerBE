package nl.shootingclub.clubmanager.exceptions;

public class AccountValidationException extends RuntimeException {
    public AccountValidationException(String message) {
        super(message);
    }
}