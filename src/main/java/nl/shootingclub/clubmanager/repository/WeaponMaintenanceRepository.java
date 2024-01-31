package nl.shootingclub.clubmanager.repository;

import nl.shootingclub.clubmanager.model.WeaponMaintenance;
import nl.shootingclub.clubmanager.model.WeaponType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeaponMaintenanceRepository extends JpaRepository<WeaponMaintenance, UUID> {

    List<WeaponMaintenance> findAllByAssociationIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(UUID associationID, LocalDateTime startDate, LocalDateTime endDate);
}
