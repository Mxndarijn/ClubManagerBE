package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "competition_user")
public class CompetitionUser {

    @EmbeddedId
    private CompetitionUserId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("competitionId")
    @JoinColumn(name = "competition_id")
    private AssociationCompetition competition;

    @OneToMany(mappedBy = "competitionUser", cascade = CascadeType.ALL)
    private Set<CompetitionScore> scores;

    @Column
    private int competitionRank;

    public CompetitionUser() {
        scores = new HashSet<>();
    }

    public List<Long> getNumericValues() {
        return scores.stream().map(CompetitionScore::getNumericValue).toList();
    }

}