package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.repository.WeaponTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WeaponTypeService {

    @Autowired
    private WeaponTypeRepository weaponTypeRepository;

    public WeaponType createWeaponType(WeaponType weaponType) {
        return weaponTypeRepository.save(weaponType);
    }

    public Optional<WeaponType> getByID(UUID weaponTypeUUID) {
        return weaponTypeRepository.findById(weaponTypeUUID);
    }

    public WeaponType saveWeaponType(WeaponType weaponType) {
        return weaponTypeRepository.save(weaponType);
    }

    public void deleteWeaponType(WeaponType weaponType) {
        weaponTypeRepository.delete(weaponType);
    }
}