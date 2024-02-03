
package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.Weapon;
import nl.shootingclub.clubmanager.model.WeaponMaintenance;
import nl.shootingclub.clubmanager.repository.WeaponMaintenanceRepository;
import nl.shootingclub.clubmanager.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeaponMaintenanceService {

    @Autowired
    private WeaponMaintenanceRepository weaponMaintenanceRepository;

    public List<WeaponMaintenance> getAllMaintenances(UUID associationID, LocalDateTime startDate, LocalDateTime endDate) {
        return weaponMaintenanceRepository.findAllByAssociationIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(associationID, startDate, endDate);
    }

    public WeaponMaintenance createWeaponMaintenance(WeaponMaintenance maintenance) {
        return weaponMaintenanceRepository.save(maintenance);
    }

    public Optional<WeaponMaintenance> getById(UUID weaponMaintenanceUUID) {
        return weaponMaintenanceRepository.findById(weaponMaintenanceUUID);
    }

    public void deleteMaintenance(WeaponMaintenance maintenance) {
        weaponMaintenanceRepository.delete(maintenance);
    }
}
