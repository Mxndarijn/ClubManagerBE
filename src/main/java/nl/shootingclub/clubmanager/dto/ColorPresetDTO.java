package nl.shootingclub.clubmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class ColorPresetDTO {

    private UUID id;
    private String colorName;
    private String primaryColor;
    private String secondaryColor;

}