package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.configuration.data.AccountPermissionData;
import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.AssociationPermission;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.AccountPermissionRepository;
import nl.shootingclub.clubmanager.repository.AssociationPermissionRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionValidationService {

    @Autowired
    private AccountPermissionRepository accountPermissionRepository;

    @Autowired
    private UserAssociationRepository userAssociationRepository;

    @Autowired
    private AssociationPermissionRepository associationPermissionRepository;


    @Autowired
    private UserRepository userRepository;

    /**
     * Cached method for validating account permissions.
     */
    @Cacheable(value = "validatePermissionSecondLayer", key = "#accountPermissionData.name + '-' + #user.id")
    public boolean validatePermissionSecondLayer(User user, AccountPermissionData accountPermissionData) {
        List<AccountPermission> permissions = accountPermissionRepository.findPermissionsByUserEmail(user.getEmail());
        Optional<AccountPermission> perm = accountPermissionRepository.findByNameEquals(accountPermissionData.getName());
        if(perm.isEmpty()) {
            return false;
        }
        return perm.filter(accountPermission -> permissions.stream().anyMatch(p -> {return p.getId().equals(accountPermission.getId());})).isPresent();
    }

    /**
     * Cached method for validating association permissions.
     */
    @Cacheable(value = "validateAssociationPermissionCache", key = "#associationUUID + '-' + #associationPermissionData.name + '-' + #user.id")
    public boolean validateAssociationPermissionSecondLayer(User user, UUID associationUUID, AssociationPermissionData associationPermissionData) {
        Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(user.getId(), associationUUID);
        if (optionalUserAssociation.isEmpty()) {
            return false;
        }

        UserAssociation userAssociation = optionalUserAssociation.get();
        Optional<AssociationPermission> optionalAssociationPermission = associationPermissionRepository.findByName(associationPermissionData.getName());
        if (optionalAssociationPermission.isEmpty()) {
            return false;
        }

        AssociationPermission associationPermission = optionalAssociationPermission.get();
        return userAssociation.getAssociationRole().getPermissions().contains(associationPermission);
    }
}
