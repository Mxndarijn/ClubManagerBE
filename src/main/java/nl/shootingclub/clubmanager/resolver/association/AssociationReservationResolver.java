
package nl.shootingclub.clubmanager.resolver.association;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.configuration.data.ReservationRepeat;
import nl.shootingclub.clubmanager.dto.CompetitionParticipateDTO;
import nl.shootingclub.clubmanager.dto.CreateReservationDTO;
import nl.shootingclub.clubmanager.dto.EditReservationSeriesDTO;
import nl.shootingclub.clubmanager.dto.response.CreateReservationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.dto.response.GetReservationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.ReservationResponseDTO;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.Track;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.model.data.ColorPreset;
import nl.shootingclub.clubmanager.model.reservation.Reservation;
import nl.shootingclub.clubmanager.model.reservation.ReservationSeries;
import nl.shootingclub.clubmanager.model.reservation.ReservationUser;
import nl.shootingclub.clubmanager.model.reservation.ReservationUserId;
import nl.shootingclub.clubmanager.repository.ReservationUserRepository;
import nl.shootingclub.clubmanager.service.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AssociationReservationResolver {

    private final ReservationService reservationService;
    private final AssociationService associationService;
    private final ReservationSeriesService reservationSeriesService;
    private final ColorPresetService colorPresetService;
    private final WeaponTypeService weaponTypeService;
    private final TrackService trackService;
    private final ReservationUserService reservationUserService;
    private final ReservationUserRepository reservationUserRepository;

    public AssociationReservationResolver(ReservationService reservationService, AssociationService associationService, ReservationSeriesService reservationSeriesService, ColorPresetService colorPresetService, WeaponTypeService weaponTypeService, TrackService trackService, ReservationUserService reservationUserService, ReservationUserRepository reservationUserRepository, ReservationUserRepository reservationUserRepository1) {
        this.reservationService = reservationService;
        this.associationService = associationService;
        this.reservationSeriesService = reservationSeriesService;
        this.colorPresetService = colorPresetService;
        this.weaponTypeService = weaponTypeService;
        this.trackService = trackService;
        this.reservationUserService = reservationUserService;
        this.reservationUserRepository = reservationUserRepository1;
    }

    @SchemaMapping(typeName = "AssociationQueries")
    public AssociationReservationResolver associationReservationQueries() {
        return this;
    }

    @SchemaMapping(typeName = "AssociationMutations")
    public AssociationReservationResolver associationReservationMutations() {
        return this;
    }

    @Observed
    @SchemaMapping(typeName = "AssociationReservationQueries")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_RESERVATIONS)")
    public GetReservationResponseDTO getReservationsBetween(@Argument UUID associationID, @Argument LocalDateTime startDate, @Argument LocalDateTime endDate) {
        GetReservationResponseDTO response = new GetReservationResponseDTO();
        Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
        if (period.getMonths() > 4) {
            response.setSuccess(false);
            return response;
        }

        response.setSuccess(true);
        response.setReservations(reservationService.getAllReservations(associationID, startDate, endDate));
        return response;

    }

    @SchemaMapping(typeName = "AssociationReservationMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateReservationResponseDTO createReservations(@Argument CreateReservationDTO dto) {
        System.out.println(dto.toString());
        validateReservationDTO(dto);

        Association association = getEntityOrThrow(
                () -> associationService.getByID(dto.getAssociationID()),
                "association-not-found"
        );

        ColorPreset preset = getEntityOrThrow(
                () -> colorPresetService.getByID(dto.getColorPreset()),
                "color-preset-not-found"
        );

        Set<Track> tracks = getTracks(dto);
        Set<WeaponType> allowedWeaponTypes = getWeaponTypes(dto);

        return switch (dto.getRepeatType()) {
            case NO_REPEAT -> createSingleReservation(dto, association, tracks, allowedWeaponTypes, preset);
            case WEEK -> createWeeklyRepeatingReservations(dto, association, tracks, allowedWeaponTypes, preset);
            case DAY -> createDailyRepeatingReservations(dto, association, tracks, allowedWeaponTypes, preset);
        };
    }

    @SchemaMapping(typeName = "AssociationReservationMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateReservationResponseDTO editReservationSeries(@Argument EditReservationSeriesDTO dto) {

        ReservationSeries series = getEntityOrThrow(
                () -> reservationSeriesService.getByID(dto.getReservationSeriesId()),
                "reservationseries-not-found"
        );

        LocalDateTime current = LocalDateTime.now();
        series.getReservations().forEach(r -> {
            if(current.isAfter(r.getEndDate())) {
                return;
            }
            if(r.getMaxSize() == series.getMaxUsers()) {
                r.setMaxSize(dto.getMaxMembers());
            }

            if(r.getTitle().equalsIgnoreCase(series.getTitle())) {
                r.setTitle(dto.getTitle());
            }
            if(r.getDescription().equalsIgnoreCase(series.getDescription())) {
                r.setDescription(dto.getDescription());
            }

            reservationService.saveReservation(r);
        });

        series.setTitle(dto.getTitle());
        series.setDescription(dto.getDescription());
        series.setMaxUsers(dto.getMaxMembers());

        reservationSeriesService.saveReservationSeries(series);

        return createReservationResponseDTO(series);
    }

    @SchemaMapping(typeName = "AssociationReservationMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public DefaultBooleanResponseDTO deleteReservationSeries(@Argument UUID seriesID, @Argument UUID associationID) {
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();

        ReservationSeries series = getEntityOrThrow(
                () -> reservationSeriesService.getByID(seriesID),
                "reservationseries-not-found"
        );

        if(!series.getAssociation().getId().equals(associationID)) {
            response.setSuccess(false);
            response.setMessage("association-not-correct");
            return response;
        }

        LocalDateTime current = LocalDateTime.now();
        series.getReservations().forEach(r -> {
            if(current.isAfter(r.getEndDate())) {
                return;
            }
            reservationService.deleteReservation(r);
        });

        reservationSeriesService.deleteReservationSeries(series);

        response.setSuccess(true);
        response.setMessage("ok");
        return response;
    }

    @SchemaMapping(typeName = "AssociationReservationMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public DefaultBooleanResponseDTO deleteReservation(@Argument UUID reservationID, @Argument UUID associationID) {
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        Reservation reservation;
        try {
            reservation = getEntityOrThrow(
                    () -> reservationService.getByID(reservationID),
                    "reservation-not-found"
            );
        } catch (IllegalArgumentException e) {
            response.setSuccess(false);
            response.setMessage("reservation-not-found");
            return response;
        }

        if(!reservation.getAssociation().getId().equals(associationID)) {
            response.setSuccess(false);
            response.setMessage("association-not-correct");
            return response;
        }

        reservationService.deleteReservation(reservation);

        response.setSuccess(true);
        response.setMessage("ok");
        return response;
    }

    private CreateReservationResponseDTO createSingleReservation(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, ColorPreset preset) {
        Reservation reservation = buildReservation(dto.getStartTime(), dto.getEndTime(), association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxSize(), preset, dto.isUserCanChooseOwnPosition());
        reservation = reservationService.createReservation(reservation);

        CreateReservationResponseDTO responseDTO = new CreateReservationResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setReservations(Set.of(reservation));
        return responseDTO;
    }

    private CreateReservationResponseDTO createWeeklyRepeatingReservations( CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, ColorPreset preset) {
        LocalDateTime startDate = dto.getStartTime();
        LocalDateTime endDate = dto.getEndTime();
        Optional<LocalDateTime> repeatUntil = dto.getRepeatUntil();
        Optional<Integer> customDaysBetween = dto.getCustomDaysBetween();

        if (!repeatUntil.isPresent() || !customDaysBetween.isPresent()) {
            throw new IllegalArgumentException("repeat-until-or-custom-days-missing");
        }

        Duration period = Duration.between(startDate.toLocalDate(), endDate.toLocalDate());


        ReservationSeries serie = new ReservationSeries();
        LocalDateTime currentDate = startDate;

        while (currentDate.isBefore(repeatUntil.get())) {
            LocalDateTime reservationEnd = currentDate.plus(period);
            Reservation reservation = buildReservation(currentDate, reservationEnd, association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxSize(),preset,  dto.isUserCanChooseOwnPosition());
            serie.getReservations().add(reservationService.createReservation(reservation));
            currentDate = currentDate.plusDays(7);
        }

        if (!serie.getReservations().isEmpty()) {
            serie.setTitle(dto.getTitle());
            serie.setDescription(dto.getDescription());
            serie.setMaxUsers(dto.getMaxSize());
            serie.setAssociation(association);
            serie = reservationSeriesService.createReservationSeries(serie);

        }

        return createReservationResponseDTO(serie);
    }

    private CreateReservationResponseDTO createDailyRepeatingReservations(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, ColorPreset preset) {
        LocalDateTime currentDate = dto.getStartTime();
        Duration period = Duration.between(dto.getStartTime().toLocalDate(), dto.getEndTime().toLocalDate());

        if (dto.getRepeatUntil().isEmpty() || dto.getCustomDaysBetween().isEmpty()) {
            throw new IllegalArgumentException("repeat-until-or-custom-days-between-missing");
        }

        ReservationSeries serie = new ReservationSeries();
        while (currentDate.isBefore(dto.getRepeatUntil().get())) {
            LocalDateTime reservationEnd = currentDate.plus(period);
            Reservation reservation = buildReservation(currentDate, reservationEnd, association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxSize(), preset, dto.isUserCanChooseOwnPosition());
            reservation = reservationService.createReservation(reservation);
            serie.getReservations().add(reservation);
            currentDate = currentDate.plusDays(dto.getCustomDaysBetween().get());
        }

        if (!serie.getReservations().isEmpty()) {
            serie.setTitle(dto.getTitle());
            serie.setDescription(dto.getDescription());
            serie.setMaxUsers(dto.getMaxSize());
            serie.setAssociation(association);
            serie = reservationSeriesService.createReservationSeries(serie);
        }

        return createReservationResponseDTO(serie);
    }

    @NotNull
    private CreateReservationResponseDTO createReservationResponseDTO(ReservationSeries serie) {

        CreateReservationResponseDTO responseDTO = new CreateReservationResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setReservations(serie.getReservations());
        responseDTO.setReservationSeries(serie);
        return responseDTO;
    }

    private <T> T getEntityOrThrow(Supplier<Optional<T>> supplier, String errorMessage) throws IllegalArgumentException{
        return supplier.get().orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    private void validateReservationDTO(CreateReservationDTO dto) {
        if(dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("start-time-after-end-time");
        }
        if(dto.getRepeatType() != ReservationRepeat.NO_REPEAT) {
            if(dto.getRepeatUntil().isEmpty())
                throw new IllegalArgumentException("repeat-until-empty");
            if(dto.getRepeatUntil().get().isAfter(LocalDateTime.now().plusYears(2)))
                throw new IllegalArgumentException("repeat-until-invalid-too-long");
        }
    }

    private Set<Track> getTracks(CreateReservationDTO dto) {
        return dto.getTracks().stream()
                .map(id -> getEntityOrThrow(() -> trackService.getByID(id), "track-not-found"))
                .collect(Collectors.toSet());
    }

    private Set<WeaponType> getWeaponTypes(CreateReservationDTO dto) {
        return dto.getAllowedWeaponTypes().stream()
                .map(id -> getEntityOrThrow(() -> weaponTypeService.getByID(id), "weapon-type-not-found"))
                .collect(Collectors.toSet());
    }

    private Reservation buildReservation(LocalDateTime start, LocalDateTime end, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, String title, String description, int maxSize, ColorPreset preset, boolean userCanChooseOwnPosition) {
        Reservation reservation = new Reservation();
        reservation.setAssociation(association);
        reservation.setTracks(tracks);
        reservation.setAllowedWeaponTypes(allowedWeaponTypes);
        reservation.setStartDate(start);
        reservation.setEndDate(end);
        reservation.setTitle(title);
        reservation.setDescription(description);
        reservation.setMaxSize(maxSize);
        reservation.setColorPreset(preset);
        reservation.setMembersCanChooseTheirOwnPosition(userCanChooseOwnPosition);
        return reservation;
    }

    @SchemaMapping(typeName = "AssociationReservationMutations")
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_RESERVATIONS)")
    public ReservationResponseDTO participateReservation(@Argument UUID associationID, @Argument CompetitionParticipateDTO dto) {
        ReservationResponseDTO responseDTO = new ReservationResponseDTO();
        Reservation reservation;
        try {
            reservation = getEntityOrThrow(
                    () -> reservationService.getByID(dto.getReservationID()),
                    "reservation-not-found"
            );
        } catch (IllegalArgumentException e) {
            responseDTO.setSuccess(false);
            return responseDTO;
        }

        if(!reservation.getAssociation().getId().equals(associationID)) {
            responseDTO.setSuccess(false);
            return responseDTO;
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<ReservationUser> optionalReservationUser = reservationUserService.findReservationUserByReservationAndUser(reservation, user);

        if(dto.isJoin()) {

            List<Integer> positionList = reservation.getReservationUsers().stream().map(ReservationUser::getPosition).toList();
            OptionalInt earliestAvailablePosition = IntStream.range(0, reservation.getMaxSize())
                    .filter(position -> !positionList.contains(position))
                    .findFirst();
            if(earliestAvailablePosition.isEmpty()) {
                responseDTO.setSuccess(false);
                return responseDTO;
            }
            int position = earliestAvailablePosition.getAsInt();

            if(reservation.isMembersCanChooseTheirOwnPosition()) {
                if (dto.getPosition() == null || dto.getPosition() < 0 || dto.getPosition() >= reservation.getMaxSize()) {
                    responseDTO.setSuccess(false);
                    return responseDTO;
                }
                Optional<ReservationUser> currentReservationUser = reservation.getReservationUsers().stream().filter(r ->
                                r.getPosition().equals(dto.getPosition()))
                        .findFirst();

                if (currentReservationUser.isPresent()) {
                    responseDTO.setSuccess(false);
                    return responseDTO;
                }
                position = dto.getPosition();
            }

            if(optionalReservationUser.isEmpty() && dto.isJoin()) {
                ReservationUser reservationUser = new ReservationUser();
                reservationUser.setReservation(reservation);
                reservationUser.setUser(user);
                reservationUser.setPosition(position);
                reservationUser.setRegisterDate(LocalDateTime.now());
                reservationUser.setId(ReservationUserId.builder().reservationId(dto.getReservationID()).userId(user.getId()).build());
                reservation.getReservationUsers().add(reservationUser);

                reservationUserService.saveReservationUser(reservationUser);
                reservationService.saveReservation(reservation);


                responseDTO.setReservation(reservation);
                responseDTO.setSuccess(true);
                return responseDTO;
            }
        }
        if(optionalReservationUser.isPresent() && !dto.isJoin()) {
            reservation.getReservationUsers().remove(optionalReservationUser.get());

            reservationUserRepository.deleteById(optionalReservationUser.get().getId());
            reservationService.saveReservation(reservation);


            responseDTO.setReservation(reservation);
            responseDTO.setSuccess(true);
            return responseDTO;
        }


        responseDTO.setSuccess(false);
        return responseDTO;

    }

}
