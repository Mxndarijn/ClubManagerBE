package nl.shootingclub.clubmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EncryptInfo {

    public EncryptInfo() {
        this.dateNow = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String byteKey;
    @Column(nullable = false)
    private LocalDateTime dateNow;
    @Column(nullable = false)
    private String pcName;

}