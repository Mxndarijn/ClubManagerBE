package nl.shootingclub.clubmanager.configuration.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.shootingclub.clubmanager.configuration.password.PasswordConstraintValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "password-invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

