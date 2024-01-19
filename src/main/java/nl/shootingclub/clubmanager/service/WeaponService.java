
package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.Weapon;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import nl.shootingclub.clubmanager.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeaponService {

    @Autowired
    private WeaponRepository weaponRepository;

    public List<Weapon> getAllWeapons() {
        return weaponRepository.findAll();
    }

    public Weapon createWeapon(Weapon weapon) {
        return weaponRepository.save(weapon);
    }

    public Optional<Weapon> getByID(UUID weaponID) {
        return weaponRepository.findById(weaponID);
    }

    public Weapon saveWeapon(Weapon weapon) {
        return weaponRepository.save(weapon);
    }
}
