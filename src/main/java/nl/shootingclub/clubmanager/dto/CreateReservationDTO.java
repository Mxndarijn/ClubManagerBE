package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.data.ReservationRepeat;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class CreateReservationDTO {
    @NotNull
    private UUID associationID;


    @NotNull
    private LocalDateTime startTime;

    @FutureOrPresent
    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private ReservationRepeat repeatType;

    @NotNull
    private Optional<LocalDateTime> repeatUntil;

    @NotNull
    private Optional<List<DayOfWeek>> repeatDays;

    @Range(min=1)
    private Optional<Integer> customDaysBetween;

    @Range(min=1)
    private Optional<Integer> customInteger;

    @NotNull
    @Size(min=1)
    private List<UUID> allowedWeaponTypes;

    @NotNull
    @Size(min=1)
    private List<UUID> tracks;

    @Range(min=1)
    private int maxMembers;


    @NotBlank(message = "empty")
    @Length(max = 50, message = "length-max-exceeded")
    private String title;

    @NotBlank(message = "empty")
    @Length(max = 200, message = "length-max-exceeded")
    private String description;
}