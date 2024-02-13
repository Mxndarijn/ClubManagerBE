package nl.shootingclub.clubmanager.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EditReservationSeriesDTO {

    @NotNull
    private UUID reservationSeriesId;
    @Range(min=1)
    private int maxMembers;


    @NotBlank(message = "empty")
    @Length(max = 50, message = "length-max-exceeded")
    private String title;

    @NotBlank(message = "empty")
    @Length(max = 200, message = "length-max-exceeded")
    private String description;
}