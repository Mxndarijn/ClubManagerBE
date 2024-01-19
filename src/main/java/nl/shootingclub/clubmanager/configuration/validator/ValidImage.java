package nl.shootingclub.clubmanager.configuration.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "image-invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    boolean spaces() default false;
}