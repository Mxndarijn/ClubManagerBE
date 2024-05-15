
package nl.shootingclub.clubmanager.resolver.association;

import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.dto.CompetitionDTO;
import nl.shootingclub.clubmanager.dto.CompetitionUserDTO;
import nl.shootingclub.clubmanager.dto.InputAssociationInviteDTO;
import nl.shootingclub.clubmanager.dto.response.CompetitionResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.AssociationCompetitionService;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AssociationCompetitionResolver {

    private final AssociationService associationService;
    private final AssociationCompetitionService associationCompetitionService;
    private final UserRepository userRepository;
    private final UserAssociationRepository userAssociationRepository;

    public AssociationCompetitionResolver(AssociationService associationService, AssociationCompetitionService associationCompetitionService, UserRepository userRepository, UserAssociationRepository userAssociationRepository) {
        this.associationService = associationService;
        this.associationCompetitionService = associationCompetitionService;
        this.userRepository = userRepository;
        this.userAssociationRepository = userAssociationRepository;
    }

    @SchemaMapping(typeName = "AssociationMutations")
    public AssociationCompetitionResolver associationCompetitionMutations() {
        return this;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_COMPETITIONS)")
    public CompetitionResponseDTO createCompetition(@Argument CompetitionDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();
        if(dto.getEndDate().isBefore(dto.getStartDate())) {
            response.setSuccess(false);
            response.setMessage("end-date-after-start-date");
            return response;
        }

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Competition competition = associationCompetitionService.createCompetition(dto, optionalAssociation.get());

        response.setSuccess(true);
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).COMPETITION_SCORE_MANAGER)")
    public CompetitionResponseDTO addUser(@Argument CompetitionUserDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<Competition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
        if(optionalCompetition.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("competition-not-found");
            return response;
        }
        Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(dto.getUserID(), associationID);
        if(optionalUserAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("user-not-found");
            return response;
        }

        Competition competition = optionalCompetition.get();

        associationCompetitionService.addUser(competition, optionalUserAssociation.get().getUser());

        response.setSuccess(true);
        response.setCompetition(competition);

        return response;
    }


}
