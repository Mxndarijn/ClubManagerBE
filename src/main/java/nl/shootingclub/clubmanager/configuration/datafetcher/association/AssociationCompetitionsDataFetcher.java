package nl.shootingclub.clubmanager.configuration.datafetcher.association;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.AssociationCompetition;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AssociationCompetitionsDataFetcher implements DataFetcher<Set<AssociationCompetition>> {
    private final UserAssociationRepository userAssociationRepository;

    public AssociationCompetitionsDataFetcher(UserAssociationRepository userAssociationRepository) {
        this.userAssociationRepository = userAssociationRepository;
    }

    @Override
    public Set<AssociationCompetition> get(DataFetchingEnvironment environment) throws Exception {
        Association association =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            List<UserAssociation> userAssociationList = userAssociationRepository.findByUserId(contextUser.getId());
            boolean hasAssociation = userAssociationList.stream()
                    .anyMatch(a -> a.getAssociation().getId().equals(association.getId()));
            if(hasAssociation)
                return association.getCompetitions();
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
