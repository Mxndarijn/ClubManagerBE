package nl.shootingclub.clubmanager.model.competition;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class CompetitionScore<T> implements Comparable<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "competition_id", referencedColumnName = "competition_id")
    })
    private CompetitionUser competitionUser;

    @Column
    private LocalDate scoreDate;

    abstract long getNumericValue();

    @Override
    public String toString() {
        return "CompetitionScore{" +
                "id=" + id +
                ", competitionUser=" + competitionUser +
                ", scoreDate=" + scoreDate +
                '}';
    }
}