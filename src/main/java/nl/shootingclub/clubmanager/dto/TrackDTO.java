package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class TrackDTO {

    @NotNull(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    private String name;

    @NotEmpty(message = "empty")
    @Length(max = 255, message = "length-max-exceeded")
    private String description;


    @NotNull
    private Set<UUID> allowedWeaponTypes;
}
