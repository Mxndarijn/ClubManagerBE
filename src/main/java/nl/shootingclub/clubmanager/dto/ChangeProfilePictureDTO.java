package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.validator.ValidImage;

import java.util.UUID;

@Getter
@Setter
public class ChangeProfilePictureDTO {

    @NotBlank
    @ValidImage
    private String image;
}
