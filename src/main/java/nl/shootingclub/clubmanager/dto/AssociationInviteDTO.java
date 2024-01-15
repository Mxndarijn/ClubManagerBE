package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Setter
public class AssociationInviteDTO {

    @NotBlank(message = "empty")
    @Email(message = "not-mail")
    @Length(max = 255, message = "length-max-exceeded")
    private String userEmail;
    private UUID associationUUID;
    private UUID associationRoleUUID;
}
