package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.converter.DurationConverter;

import java.time.Duration;

@Setter
@Builder
@Getter
@AllArgsConstructor
@Entity
public class CompetitionScoreTime extends CompetitionScore<CompetitionScoreTime> {
    @Convert(converter = DurationConverter.class)
    @Column(name = "score")
    private Duration score;

    public CompetitionScoreTime() {

    }

    @Override
    public int compareTo(CompetitionScoreTime o) {
        return this.score.compareTo(o.score);
    }

    @Override
    long getNumericValue() {
        return score.toNanos();
    }
}