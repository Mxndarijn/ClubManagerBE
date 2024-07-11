package nl.shootingclub.clubmanager.configuration.datafetcher.association;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.service.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AssociationWelcomeMessageDataFetcher implements DataFetcher<String> {
    private final UserAssociationRepository userAssociationRepository;

    public AssociationWelcomeMessageDataFetcher(UserAssociationRepository userAssociationRepository) {
        this.userAssociationRepository = userAssociationRepository;
    }

    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        Association association =environment.getSource();

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            List<UserAssociation> userAssociationList = userAssociationRepository.findByUserId(contextUser.getId());
            boolean hasAssociation = userAssociationList.stream()
                    .anyMatch(a -> a.getAssociation().getId().equals(association.getId()));
            if(hasAssociation)
                return association.getWelcomeMessage();
        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
