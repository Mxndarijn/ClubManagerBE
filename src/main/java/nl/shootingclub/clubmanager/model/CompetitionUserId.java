package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@Setter
@Embeddable
public class CompetitionUserId implements Serializable {

    @Column(name = "competition_id")
    private UUID userId;

    @Column(name = "competition_id")
    private UUID competitionId;

    public CompetitionUserId() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionUserId that = (CompetitionUserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(competitionId, that.competitionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, competitionId);
    }
}
