package nl.shootingclub.clubmanager.configuration.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {
    String message() default "field-invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    boolean spaces() default false;
}