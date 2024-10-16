package nl.shootingclub.clubmanager.model.competition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
@Entity
public class CompetitionScorePoint extends CompetitionScore<CompetitionScorePoint> {

    @Column(name = "score")
    private long score;

    public CompetitionScorePoint() {

    }

    @Override
    public int compareTo(CompetitionScorePoint o) {
        return Long.compare(this.score, o.score);
    }

    @Override
    long getNumericValue() {
        return score;
    }

    @Override
    public String toString() {
        return "CompetitionScorePoint{" +
                "score=" + score +
                '}';
    }
}