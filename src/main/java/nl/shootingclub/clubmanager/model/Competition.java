package nl.shootingclub.clubmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.shootingclub.clubmanager.configuration.data.CompetitionRanking;
import nl.shootingclub.clubmanager.configuration.data.CompetitionScoreType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "competition")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column()
    private String name;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "association_id", referencedColumnName = "id")
    private Association association;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "competition")
    private Set<CompetitionUser> competitionUsers;

    @Column(name = "score_type")
    private CompetitionScoreType scoreType;

    @Column(name = "ranking")
    private CompetitionRanking ranking;

    @Column(name = "active")
    private boolean active;


    public void recalculateRanking() {
        getScoreType().getHandlerInstance().recalculateRanking(this);

    }


}
