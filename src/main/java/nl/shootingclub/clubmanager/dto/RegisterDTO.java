package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.password.ValidPassword;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "empty")
    @Email(message = "not-mail")
    @Length(max = 255, message = "length-max-exceeded")
    private String email;

    @NotBlank(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    @ValidPassword(message = "not-valid")
    private String password;

    @NotBlank(message = "empty")
    @Length(max = 255, min = 2, message = "length-wrong")
    private String firstName;

    @Length(max = 255, min = 2, message = "length-wrong")
    @NotBlank(message = "empty")
    private String lastName;
}
