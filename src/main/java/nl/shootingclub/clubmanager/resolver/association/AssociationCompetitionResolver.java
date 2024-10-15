
package nl.shootingclub.clubmanager.resolver.association;

import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.dto.response.CompetitionResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.model.competition.AssociationCompetition;
import nl.shootingclub.clubmanager.model.competition.CompetitionUser;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.AssociationCompetitionService;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @SchemaMapping(typeName = "AssociationQueries")
    public AssociationCompetitionResolver associationCompetitionQueries() {
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

        AssociationCompetition competition = associationCompetitionService.createCompetition(dto, optionalAssociation.get());

        response.setSuccess(true);
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_COMPETITIONS)")
    public DefaultBooleanResponseDTO deleteCompetition(@Argument UUID competitionId, @Argument UUID associationID) {
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(competitionId);
        if(optionalCompetition.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("competition-not-found");
            return response;
        }
        associationCompetitionService.deleteCompetition(optionalCompetition.get());
        response.setSuccess(true);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_COMPETITIONS)")
    public CompetitionResponseDTO stopCompetition(@Argument UUID competitionId, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(competitionId);
        if(optionalCompetition.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("competition-not-found");
            return response;
        }
        optionalCompetition.get().setActive(false);
        associationCompetitionService.saveCompetition(optionalCompetition.get());
        response.setSuccess(true);
        response.setCompetition(optionalCompetition.get());

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

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
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

        AssociationCompetition competition = optionalCompetition.get();

        associationCompetitionService.addUser(competition, optionalUserAssociation.get().getUser());

        response.setSuccess(true);
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).COMPETITION_SCORE_MANAGER)")
    public CompetitionResponseDTO removeUser(@Argument CompetitionUserDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
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

        AssociationCompetition competition = optionalCompetition.get();



        response.setSuccess( associationCompetitionService.removeUser(competition, optionalUserAssociation.get().getUser()));
        response.setMessage("");
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).COMPETITION_SCORE_MANAGER)")
    public CompetitionResponseDTO addUserScore(@Argument CompetitionScoreDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
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

        Optional<CompetitionUser> optionalCompetitionUser = associationCompetitionService.getCompetitionUser(dto.getCompetitionID(), dto.getUserID());
        if(optionalCompetitionUser.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("user-competition-not-found");
            return response;
        }

        AssociationCompetition competition = optionalCompetition.get();

        associationCompetitionService.addUserScore(optionalCompetitionUser.get(), dto.getScore(), dto.getScoreDate());

        response.setSuccess(true);
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).COMPETITION_SCORE_MANAGER)")
    public CompetitionResponseDTO addUserScores(@Argument CompetitionScoresDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
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

        Optional<CompetitionUser> optionalCompetitionUser = associationCompetitionService.getCompetitionUser(dto.getCompetitionID(), dto.getUserID());
        if(optionalCompetitionUser.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("user-competition-not-found");
            return response;
        }

        AssociationCompetition competition = optionalCompetition.get();
        dto.getScores().forEach(score -> {
            associationCompetitionService.addUserScore(optionalCompetitionUser.get(), score.getScore(), score.getScoreDate());
        });

        response.setSuccess(true);
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).COMPETITION_SCORE_MANAGER)")
    public CompetitionResponseDTO removeUserScore(@Argument CompetitionRemoveScoreDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
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

        Optional<CompetitionUser> optionalCompetitionUser = associationCompetitionService.getCompetitionUser(dto.getCompetitionID(), dto.getUserID());
        if(optionalCompetitionUser.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("user-competition-not-found");
            return response;
        }

        AssociationCompetition competition = optionalCompetition.get();



        response.setSuccess(associationCompetitionService.removeUserScore(optionalCompetitionUser.get(), dto.getScoreId()));
        response.setCompetition(competition);

        return response;
    }

    @SchemaMapping(typeName = "AssociationCompetitionMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).COMPETITION_SCORE_MANAGER)")
    public CompetitionResponseDTO removeUserScores(@Argument CompetitionRemoveScoresDTO dto, @Argument UUID associationID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(dto.getCompetitionID());
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

        Optional<CompetitionUser> optionalCompetitionUser = associationCompetitionService.getCompetitionUser(dto.getCompetitionID(), dto.getUserID());
        if(optionalCompetitionUser.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("user-competition-not-found");
            return response;
        }

        AssociationCompetition competition = optionalCompetition.get();


        AtomicBoolean success = new AtomicBoolean(true);
        dto.getScores().forEach(score -> {
            if(!associationCompetitionService.removeUserScore(optionalCompetitionUser.get(), score)) {
                success.set(false);
            }
        });

        response.setSuccess(success.get());
        response.setCompetition(competition);

        return response;
    }


    @SchemaMapping(typeName = "AssociationCompetitionQueries")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_RESERVATIONS)") //TODO MAKE OWN PERMISSION
    public CompetitionResponseDTO getCompetitionInformation(@Argument UUID associationID, @Argument UUID competitionID) {
        CompetitionResponseDTO response = new CompetitionResponseDTO();

        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if (optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("association-not-found");
            return response;
        }

        Optional<AssociationCompetition> optionalCompetition = associationCompetitionService.getCompetitionById(competitionID);
        if (optionalCompetition.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("competition-not-found");
            return response;
        }


        response.setSuccess(true);
        response.setCompetition(optionalCompetition.get());
        return response;
    }
}
