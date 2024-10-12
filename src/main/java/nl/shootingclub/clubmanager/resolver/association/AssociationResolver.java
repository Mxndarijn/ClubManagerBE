
package nl.shootingclub.clubmanager.resolver.association;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.dto.AssociationStatisticsDTO;
import nl.shootingclub.clubmanager.exceptions.UserNotFoundException;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;

@Controller
public class AssociationResolver {

    private final AssociationService associationService;
    private final UserRepository userRepository;
    private final UserAssociationRepository userAssociationRepository;
    private final AssociationRepository associationRepository;

    public AssociationResolver(AssociationService associationService, UserRepository userRepository, UserAssociationRepository userAssociationRepository, AssociationRepository associationRepository) {
        this.associationService = associationService;
        this.userRepository = userRepository;
        this.userAssociationRepository = userAssociationRepository;
        this.associationRepository = associationRepository;
    }

    @QueryMapping
    public AssociationResolver associationQueries() {
        return this;
    }

    @MutationMapping
    public AssociationResolver associationMutations() {
        return this;
    }

    /**
     * Retrieves the details of an association.
     *
     * @param associationID The ID of the association to retrieve.
     * @return The Association object containing the details of the association.
     *         Returns null if the user does not have permission to manage the association or if the association is not found.
     * @throws UserNotFoundException If the authenticated user is not found in the user repository.
     */
    //TODO bekijk hier nog eens goed naar, permission klopt????
    @SchemaMapping(typeName = "AssociationQueries")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_TRACKS)")
    @Observed
    public Association getAssociationDetails(@Argument UUID associationID) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByEmailEquals(user.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("user-not-found");
        }
        User u = optionalUser.get();
        Optional<Association> optionalAssociation = associationRepository.findByUserIDAndAssociationID(u.getId(), associationID);
        System.out.println(optionalAssociation.get().getName());
        return optionalAssociation.orElse(null);

    }

    /**
     * Retrieves the statistics of an association.
     *
     * @param associationID The ID of the association.
     * @return The AssociationStatisticsDTO object containing the total number of members, weapons, and tracks of the association,
     *          or null if the association does not exist.
     */
    //TODO Permission wrong?
    @SchemaMapping(typeName = "AssociationQueries")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_MEMBERS)")
    public AssociationStatisticsDTO getAssociationStatistics(@Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty())
            return null;

        Association association = optionalAssociation.get();

        AssociationStatisticsDTO statisticsDTO = new AssociationStatisticsDTO();
        statisticsDTO.setTotalMembers(association.getUsers().size());
        statisticsDTO.setTotalTracks(association.getTracks().size());
        statisticsDTO.setTotalWeapons(association.getWeapons().size());

        return statisticsDTO;
    }

}
