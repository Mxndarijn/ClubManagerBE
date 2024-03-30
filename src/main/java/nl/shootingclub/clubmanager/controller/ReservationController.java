package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.data.ReservationRepeat;
import nl.shootingclub.clubmanager.dto.CreateReservationDTO;
import nl.shootingclub.clubmanager.dto.EditReservationSeriesDTO;
import nl.shootingclub.clubmanager.dto.GetWeaponMaintenancesDTO;
import nl.shootingclub.clubmanager.dto.response.CreateReservationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.dto.response.GetReservationResponseDTO;
import nl.shootingclub.clubmanager.dto.response.ReservationResponseDTO;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.service.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Controller
public class ReservationController {

    @Autowired
    private AssociationService associationService;


    @Autowired
    private TrackService trackService;


    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationSeriesService reservationSeriesService;

    @Autowired
    private WeaponTypeService weaponTypeService;

    @Autowired
    private ColorPresetService colorPresetService;

    @Autowired
    private ReservationUserService reservationUserService;

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateReservationResponseDTO createReservations(@Argument CreateReservationDTO dto) {
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

    @MutationMapping
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

    @MutationMapping
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

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public DefaultBooleanResponseDTO deleteReservation(@Argument UUID reservationID, @Argument UUID associationID) {
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();

        Reservation reservation = getEntityOrThrow(
                () -> reservationService.getByID(reservationID),
                "reservation-not-found"
        );

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
        Reservation reservation = buildReservation(dto.getStartTime(), dto.getEndTime(), association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers(), preset);
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

        Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());

        ReservationSeries serie = new ReservationSeries();
        LocalDateTime currentDate = startDate;

        while (currentDate.isBefore(repeatUntil.get())) {
                LocalDateTime reservationEnd = currentDate.plus(period);
                Reservation reservation = buildReservation(currentDate, reservationEnd, association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers(), preset);
                serie.getReservations().add(reservationService.createReservation(reservation));
            currentDate = currentDate.plusDays(7);
        }

        if (!serie.getReservations().isEmpty()) {
            serie.setTitle(dto.getTitle());
            serie.setDescription(dto.getDescription());
            serie.setMaxUsers(dto.getMaxMembers());
            serie.setAssociation(association);
            serie = reservationSeriesService.createReservationSeries(serie);

        }

        return createReservationResponseDTO(serie);
    }
    private static int getWeekNumber(LocalDateTime time) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return time.get(weekFields.weekOfWeekBasedYear());
    }

    private CreateReservationResponseDTO createDailyRepeatingReservations(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, ColorPreset preset) {
        LocalDateTime currentDate = dto.getStartTime();
        Period period = Period.between(dto.getStartTime().toLocalDate(), dto.getEndTime().toLocalDate());

        if (dto.getRepeatUntil().isEmpty() || dto.getCustomDaysBetween().isEmpty()) {
            throw new IllegalArgumentException("repeat-until-or-custom-days-between-missing");
        }

        ReservationSeries serie = new ReservationSeries();
        while (currentDate.isBefore(dto.getRepeatUntil().get())) {
            LocalDateTime reservationEnd = currentDate.plus(period);
            Reservation reservation = buildReservation(currentDate, reservationEnd, association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers(), preset);
            reservation = reservationService.createReservation(reservation);
            serie.getReservations().add(reservation);
            currentDate = currentDate.plusDays(dto.getCustomDaysBetween().get());
        }

        if (!serie.getReservations().isEmpty()) {
            serie.setTitle(dto.getTitle());
            serie.setDescription(dto.getDescription());
            serie.setMaxUsers(dto.getMaxMembers());
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

    private <T> T getEntityOrThrow(Supplier<Optional<T>> supplier, String errorMessage) {
        return supplier.get().orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    private void validateReservationDTO(CreateReservationDTO dto) {
        if(dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("start-time-after-end-time");
        }
        if(dto.getRepeatType() != ReservationRepeat.NO_REPEAT) {
            if(dto.getRepeatUntil().isEmpty())
                throw new IllegalArgumentException("repeat-until-empty");
            if(dto.getRepeatUntil().get().isBefore(LocalDateTime.now().plusYears(2)))
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

    @QueryMapping
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

    private Reservation buildReservation(LocalDateTime start, LocalDateTime end, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, String title, String description, int maxSize, ColorPreset preset) {
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
        return reservation;
    }


    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).VIEW_RESERVATIONS)")
    public ReservationResponseDTO participateReservation(@Argument UUID associationID, UUID reservationID, boolean join) {
        Reservation reservation = getEntityOrThrow(
                () -> reservationService.getByID(reservationID),
                "reservation-not-found"
        );

        ReservationResponseDTO responseDTO = new ReservationResponseDTO();
//        Association association = getEntityOrThrow(
//                () -> associationService.getByID(associationID),
//                "association-not-found"
//        );
        if(!reservation.getAssociation().getId().equals(associationID)) {
            responseDTO.setSuccess(false);
            return responseDTO;
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<ReservationUser> optionalReservationUser = reservationUserService.findReservationUserByReservationAndUser(reservation, user);

        responseDTO.setReservation(reservation);
        if(optionalReservationUser.isEmpty() && join) {
            ReservationUser reservationUser = new ReservationUser();
            reservationUser.setReservation(reservation);
            reservationUser.setUser(user);
            reservation.getReservationUsers().add(reservationUser);

            reservationService.saveReservation(reservation);

            responseDTO.setSuccess(true);
            return responseDTO;
        }
        if(optionalReservationUser.isPresent() && !join) {
            reservation.getReservationUsers().remove(optionalReservationUser.get());

            reservationService.saveReservation(reservation);

            responseDTO.setSuccess(true);
            return responseDTO;
        }


        responseDTO.setSuccess(false);
        return responseDTO;

    }
}