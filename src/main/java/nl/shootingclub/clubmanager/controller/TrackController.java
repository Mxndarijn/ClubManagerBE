
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.dto.*;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class TrackController {

    @Autowired
    private AssociationService associationService;

    @Autowired
    private WeaponTypeRepository weaponTypeRepository;

    @Autowired
    private TrackService trackService;

    @QueryMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_TRACKS)")
    public Set<Track> getTracksOfAssociation(@Argument UUID associationID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        if(optionalAssociation.isPresent()) {
            Association association = optionalAssociation.get();
            return association.getTracks();
        }
        return new HashSet<>();
    }

    @MutationMapping
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

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateTrackResponseDTO editTrackForAssociation(@Argument UUID associationID, @Argument TrackDTO dto, @Argument UUID trackUUID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        CreateTrackResponseDTO response = new CreateTrackResponseDTO();
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("could-not-find-association");
            return response;
        }

        Optional<Track> optionalTrack = trackService.getByID(trackUUID);
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

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public DefaultBooleanResponseDTO deleteTrackForAssociation(@Argument UUID associationID, @Argument UUID trackUUID) {
        Optional<Association> optionalAssociation = associationService.getByID(associationID);
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        if(optionalAssociation.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("could-not-find-association");
            return response;
        }

        Optional<Track> optionalTrack = trackService.getByID(trackUUID);
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
