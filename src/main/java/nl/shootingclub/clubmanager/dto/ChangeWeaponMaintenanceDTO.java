package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChangeWeaponMaintenanceDTO {

    @NotNull(message = "empty")
    private UUID weaponUUID;
    @NotNull(message = "empty")
    private UUID associationUUID;

    @NotNull(message = "empty")
    private UUID weaponMaintenanceUUID;
    @NotNull(message = "empty")
    private UUID colorPresetUUID;

    @NotBlank(message = "empty")
    @Length(max = 50, message = "length-max-exceeded")
    private String title;

    @NotBlank(message = "empty")
    @Length(max = 200, message = "length-max-exceeded")
    private String description;



    @NotNull(message = "Provide a start date")
    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDateTime startDate;

    @NotNull(message = "Provide an end date")
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime endDate;
}
