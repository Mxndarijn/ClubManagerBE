package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData;
import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AccountPermissionRepository;
import nl.shootingclub.clubmanager.repository.AssociationPermissionRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PermissionService {

    @Autowired
    private AccountPermissionRepository accountPermissionRepository;

    @Autowired
    private UserAssociationRepository userAssociationRepository;

    @Autowired
    private AssociationPermissionRepository associationPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean validatePermission(AccountPermissionData accountPermissionData) {
        try {
            if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User tempUser)) {
                return false;
            }
            Optional<User> optionalUser = userRepository.findById(tempUser.getId());
            if(optionalUser.isEmpty()) {
                return false;
            }
            User user = optionalUser.get();
            Optional<AccountPermission> perm = accountPermissionRepository.findByNameEquals(accountPermissionData.getName());
            return perm.filter(accountPermission -> user.getRole().getPermissions().contains(accountPermission)).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAssociationPermission(UUID associationUUID, AssociationPermissionData associationPermissionData) {
        try {
            if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User tempUser)) {
                return false;
            }
            Optional<UserAssociation> optionalUserAssociation = userAssociationRepository.findByUserIdAndAssociationId(tempUser.getId(), associationUUID);
            if(optionalUserAssociation.isEmpty())
                return false;
            UserAssociation userAssociation = optionalUserAssociation.get();
            Optional<AssociationPermission> optionalAssociationPermission = associationPermissionRepository.findByName(associationPermissionData.getName());
            if(optionalAssociationPermission.isEmpty()) {
                return false;
            }
            AssociationPermission associationPermission = optionalAssociationPermission.get();
            return userAssociation.getAssociationRole().getPermissions().contains(associationPermission);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AccountPermission> getMyPermissions(User user) {
        return new ArrayList<>(user.getRole().getPermissions());
    }

}
