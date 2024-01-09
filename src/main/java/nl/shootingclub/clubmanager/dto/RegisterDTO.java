package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.validator.ValidName;
import nl.shootingclub.clubmanager.configuration.validator.ValidPassword;
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
    @Length(max = 64, min = 2, message = "length-wrong")
    @ValidName(spaces = true, message = "invalid-characters")
    private String fullName;
}
