package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.data.ReservationRepeat;
import nl.shootingclub.clubmanager.dto.CreateReservationDTO;
import nl.shootingclub.clubmanager.dto.response.CreateReservationResponseDTO;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AssociationRoleRepository;
import nl.shootingclub.clubmanager.repository.DefaultImageRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.Period;
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
    private WeaponTypeService weaponTypeService;

    @MutationMapping
    @PreAuthorize("@permissionService.validateAssociationPermission(#dto.associationID, T(nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData).MANAGE_TRACK_CONFIGURATION)")
    public CreateReservationResponseDTO createReservations(@Argument CreateReservationDTO dto) {
        validateReservationDTO(dto);

        Association association = getEntityOrThrow(
                () -> associationService.getByID(dto.getAssociationID()),
                "association-not-found"
        );

        Set<Track> tracks = getTracks(dto);
        Set<WeaponType> allowedWeaponTypes = getWeaponTypes(dto);

        switch (dto.getRepeatType()) {
            case NO_REPEAT:
                return createSingleReservation(dto, association, tracks, allowedWeaponTypes);
            case CUSTOM:
                return createCustomRepeatingReservations(dto, association, tracks, allowedWeaponTypes);
            case WEEK:
                return createWeeklyRepeatingReservations(dto, association, tracks, allowedWeaponTypes);
            case DAY:
                return createDailyRepeatingReservations(dto, association, tracks, allowedWeaponTypes);
            default:
                throw new IllegalArgumentException("invalid-repeat-type");
        }
    }

    private CreateReservationResponseDTO createSingleReservation(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes) {
        Reservation reservation = buildReservation(dto.getStartTime(), dto.getEndTime(), association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers());
        reservation = reservationService.createReservation(reservation);

        CreateReservationResponseDTO responseDTO = new CreateReservationResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setReservations(List.of(reservation));
        return responseDTO;
    }

    private CreateReservationResponseDTO createCustomRepeatingReservations(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes) {
        LocalDateTime currentDate = dto.getStartTime();

        Period period = Period.between(dto.getStartTime().toLocalDate(), dto.getEndTime().toLocalDate());
        if(dto.getCustomInteger().isEmpty() || dto.getCustomDaysBetween().isEmpty()) {
            throw new IllegalArgumentException("custom-integer-or-custom-days-between-empty");
        }

        ReservationSeries serie = new ReservationSeries();
        ArrayList<Reservation> reservations = new ArrayList<>();
        while (currentDate.isBefore(dto.getRepeatUntil().get())) {
            Reservation reservation = buildReservation(currentDate, currentDate.plus(period), association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers());
            reservation = reservationService.createReservation(reservation);
            reservations.add(reservation);
            serie.getReservations().add(reservation);
            currentDate = currentDate.plusDays((long) dto.getCustomInteger().get() * dto.getCustomDaysBetween().get());
        }

        CreateReservationResponseDTO responseDTO = new CreateReservationResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setReservations(reservations);
        responseDTO.setReservationSeries(serie);
        return responseDTO;
    }

    private CreateReservationResponseDTO createWeeklyRepeatingReservations(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes) {
        if(dto.getRepeatDays().isEmpty())
            throw new IllegalArgumentException("repeat-days-empty");
        LocalDateTime currentDate = dto.getStartTime();
        Period period = Period.between(dto.getStartTime().toLocalDate(), dto.getEndTime().toLocalDate());

        ReservationSeries serie = new ReservationSeries();
        ArrayList<Reservation> reservations = new ArrayList<>();
        while(currentDate.isBefore(dto.getRepeatUntil().get())) {
            if(dto.getRepeatDays().get().contains(currentDate.getDayOfWeek())) {
                Reservation reservation = buildReservation(currentDate, currentDate.plus(period), association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers());
                reservation = reservationService.createReservation(reservation);
                reservations.add(reservation);
                serie.getReservations().add(reservation);
            }
            currentDate = currentDate.plusDays(1);
        }

        CreateReservationResponseDTO responseDTO = new CreateReservationResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setReservations(reservations);
        responseDTO.setReservationSeries(serie);
        return responseDTO;
    }

    private CreateReservationResponseDTO createDailyRepeatingReservations(CreateReservationDTO dto, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes) {
        LocalDateTime currentDate = dto.getStartTime();
        Period period = Period.between(dto.getStartTime().toLocalDate(), dto.getEndTime().toLocalDate());

        ReservationSeries serie = new ReservationSeries();
        ArrayList<Reservation> reservations = new ArrayList<>();
        while(currentDate.isBefore(dto.getRepeatUntil().get())) {
            Reservation reservation = buildReservation(currentDate, currentDate.plus(period), association, tracks, allowedWeaponTypes, dto.getTitle(), dto.getDescription(), dto.getMaxMembers());
            reservation = reservationService.createReservation(reservation);
            reservations.add(reservation);
            serie.getReservations().add(reservation);
            currentDate = currentDate.plusDays(1);
        }

        CreateReservationResponseDTO responseDTO = new CreateReservationResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setReservations(reservations);
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

    private Reservation buildReservation(LocalDateTime start, LocalDateTime end, Association association, Set<Track> tracks, Set<WeaponType> allowedWeaponTypes, String title, String description, int maxSize) {
        Reservation reservation = new Reservation();
        reservation.setAssociation(association);
        reservation.setTracks(tracks);
        reservation.setAllowedWeaponTypes(allowedWeaponTypes);
        reservation.setStartDate(start);
        reservation.setEndDate(end);
        reservation.setTitle(title);
        reservation.setDescription(description);
        reservation.setMaxSize(maxSize);
        return reservation;
    }
}