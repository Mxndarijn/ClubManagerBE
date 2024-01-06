package nl.shootingclub.clubmanager.configuration.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameConstraintValidator implements ConstraintValidator<ValidName, String> {

    private boolean allowSpaces;

    @Override
    public void initialize(ValidName constraintAnnotation) {
        this.allowSpaces = constraintAnnotation.spaces();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if(name == null) {
            return false;
        }
        if(allowSpaces) {
            return name.matches("^[a-zA-Z]+( [a-zA-Z]+)*$");
        }
        return name.matches("^[a-zA-Z]+$");
    }
}