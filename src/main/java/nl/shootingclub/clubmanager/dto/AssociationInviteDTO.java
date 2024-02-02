package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Setter
public class AssociationInviteDTO {

    @NotNull(message = "empty")
    @Email(message = "not-mail")
    @Length(max = 255, message = "length-max-exceeded")
    private String userEmail;

    @NotBlank
    private UUID associationUUID;

    @NotBlank
    private UUID associationRoleUUID;
}
