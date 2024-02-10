package nl.shootingclub.clubmanager.dto.response;

import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.model.Weapon;

@Getter
@Setter
public class CreateWeaponResponseDTO {

    private boolean success;
    private String message;
    private Weapon weapon;
}
