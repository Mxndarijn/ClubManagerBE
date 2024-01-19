package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.validator.ValidName;
import nl.shootingclub.clubmanager.configuration.validator.ValidPassword;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Setter
public class UpdateMyProfileDTO {

    @NotBlank(message = "empty")
    @Email(message = "not-mail")
    @Length(max = 255, message = "length-max-exceeded")
    @ValidName()
    private String fullName;

    @NotBlank(message = "empty")
    @Email(message = "not-mail")
    @Length(max = 255, message = "length-max-exceeded")
    private String email;

    @NotBlank(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    @ValidPassword(message = "not-valid")
    private String oldPassword;

    @Length(max = 255, message = "length-max-exceeded")
    @ValidPassword(message = "not-valid", canBeNull = true)
    private String newPassword;
}
