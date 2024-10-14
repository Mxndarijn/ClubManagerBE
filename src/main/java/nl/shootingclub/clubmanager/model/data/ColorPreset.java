package nl.shootingclub.clubmanager.model.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "color_preset")
public class ColorPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Lob()
    @Column(name = "color-name", nullable = false)
    private String colorName;


    @Column(name = "primary-color", nullable = false)
    private String primaryColor;

    @Column(name = "secondary-color", nullable = false)
    private String secondaryColor;

}