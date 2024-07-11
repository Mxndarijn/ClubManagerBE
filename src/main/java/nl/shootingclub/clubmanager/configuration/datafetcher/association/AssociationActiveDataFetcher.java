package nl.shootingclub.clubmanager.configuration.datafetcher.association;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociationActiveDataFetcher implements DataFetcher<Boolean> {
    private final UserAssociationRepository userAssociationRepository;

    public AssociationActiveDataFetcher(UserAssociationRepository userAssociationRepository) {
        this.userAssociationRepository = userAssociationRepository;
    }

    @Override
    public Boolean get(DataFetchingEnvironment environment) throws Exception {
        Association association =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            List<UserAssociation> userAssociationList = userAssociationRepository.findByUserId(contextUser.getId());
            boolean hasAssociation = userAssociationList.stream()
                    .anyMatch(a -> a.getAssociation().getId().equals(association.getId()));
            if(hasAssociation)
                return association.isActive();
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
