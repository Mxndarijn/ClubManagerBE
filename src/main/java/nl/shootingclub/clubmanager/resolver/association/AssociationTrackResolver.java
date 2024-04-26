
package nl.shootingclub.clubmanager.resolver.association;

import nl.shootingclub.clubmanager.dto.TrackDTO;
import nl.shootingclub.clubmanager.dto.response.CreateTrackResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.dto.response.GetReservationResponseDTO;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.repository.WeaponTypeRepository;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.ReservationService;
import nl.shootingclub.clubmanager.service.TrackService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
public class AssociationTrackResolver {

    private final AssociationService associationService;
    private final TrackService trackService;
    private final WeaponTypeRepository weaponTypeRepository;

    public AssociationTrackResolver(AssociationService associationService, TrackService trackService, WeaponTypeRepository weaponTypeRepository) {
        this.associationService = associationService;
        this.trackService = trackService;
        this.weaponTypeRepository = weaponTypeRepository;
    }

    @SchemaMapping(typeName = "AssociationQueries")
    public AssociationTrackResolver associationTrackQueries() {
        return this;
    }

    @SchemaMapping(typeName = "AssociationMutations")
    public AssociationTrackResolver associationTrackMutations() {
        return this;
    }

    @SchemaMapping(typeName = "AssociationTrackQueries")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_TRACKS)")
    public Set<Track> getTracksOfAssociation(@Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isPresent()) {
            Association association = optionalAssociation.get();
            return association.getTracks();
        }
        return new HashSet<>();
    }

    @SchemaMapping(typeName = "AssociationTrackMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateTrackResponseDTO createTrackForAssociation(@Argument UUID associationID, @Argument TrackDTO dto) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        CreateTrackResponseDTO response = new CreateTrackResponseDTO();
        if(optionalAssociation.isPresent()) {
            Association association = optionalAssociation.get();
            Track track = new Track();
            track.setName(dto.getName());
            track.setDescription(dto.getDescription());
            track.setAssociation(association);

            Set<WeaponType> types = new HashSet<>();
            for (UUID typeId : dto.getAllowedWeaponTypes()) {
                Optional<WeaponType> optionalType = weaponTypeRepository.findById(typeId);
                optionalType.ifPresent(types::add);
            }
            track.setAllowedWeaponTypes(types);
            track = trackService.saveTrack(track);

            response.setSuccess(true);
            response.setMessage("ok");
            response.setTrack(track);
            return response;
        }
        response.setSuccess(false);
        response.setMessage("could-not-find-association");
        return response;
    }

    @SchemaMapping(typeName = "AssociationTrackMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateTrackResponseDTO editTrackForAssociation(@Argument UUID associationID, @Argument TrackDTO dto, @Argument UUID trackID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        CreateTrackResponseDTO response = new CreateTrackResponseDTO();
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("could-not-find-association");
            return response;
        }

        Optional<Track> optionalTrack = trackService.getByID(trackID);
        if(optionalTrack.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("could-not-find-track");
            return response;
        }
        Track track = optionalTrack.get();
        track.setName(dto.getName());
        track.setDescription(dto.getDescription());

        Set<WeaponType> types = new HashSet<>();
        for (UUID typeId : dto.getAllowedWeaponTypes()) {
            Optional<WeaponType> optionalType = weaponTypeRepository.findById(typeId);
            optionalType.ifPresent(types::add);
        }
        track.setAllowedWeaponTypes(types);
        track = trackService.saveTrack(track);
        response.setSuccess(true);
        response.setMessage("ok");
        response.setTrack(track);
        return response;
    }

    @SchemaMapping(typeName = "AssociationTrackMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public DefaultBooleanResponseDTO deleteTrackForAssociation(@Argument UUID associationID, @Argument UUID trackID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("could-not-find-association");
            return response;
        }

        Optional<Track> optionalTrack = trackService.getByID(trackID);
        if(optionalTrack.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("could-not-find-track");
            return response;
        }
        trackService.deleteTrack(optionalTrack.get());
        response.setSuccess(true);
        response.setMessage("ok");
        return response;
    }

}
