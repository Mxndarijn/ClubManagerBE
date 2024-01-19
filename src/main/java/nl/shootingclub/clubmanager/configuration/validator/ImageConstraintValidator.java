package nl.shootingclub.clubmanager.configuration.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.shootingclub.clubmanager.configuration.validator.ValidImage;

import java.util.regex.Pattern;

public class ImageConstraintValidator implements ConstraintValidator<ValidImage, String> {

    private static final Pattern DATA_URL_PATTERN = Pattern.compile("data:(.+?);base64,(.*)");

    private boolean allowSpaces;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        this.allowSpaces = constraintAnnotation.spaces();
    }

    @Override
    public boolean isValid(String encodedImage, ConstraintValidatorContext context) {
        if (encodedImage == null || encodedImage.isEmpty()) {
            return false;
        }

        return DATA_URL_PATTERN.matcher(encodedImage).matches();
    }
}