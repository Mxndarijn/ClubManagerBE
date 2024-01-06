package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Getter
@Setter
@ControllerAdvice
public class LoginDTO {

    @NotBlank(message = "empty")
    @Email(message = "not-mail")
    @Length(max = 255, message = "length-max-exceeded")
    private String email;

    @NotBlank(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    private String password;
}
