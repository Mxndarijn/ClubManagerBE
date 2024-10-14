package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.configuration.data.AccountPermissionData;
import nl.shootingclub.clubmanager.configuration.data.AssociationPermissionData;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.account.AccountPermission;
import nl.shootingclub.clubmanager.repository.AccountPermissionRepository;
import nl.shootingclub.clubmanager.repository.AssociationPermissionRepository;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private PermissionValidationService permissionValidationService;

    public boolean validatePermission(AccountPermissionData accountPermissionData) {
        try {
            if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User tempUser)) {
                return false;
            }
            return permissionValidationService.validatePermissionSecondLayer(tempUser, accountPermissionData);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAssociationPermission(UUID associationUUID, AssociationPermissionData associationPermissionData) {
        try {
            if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User tempUser)) {
                return false;
            }
            return permissionValidationService.validateAssociationPermissionSecondLayer(tempUser, associationUUID, associationPermissionData);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AccountPermission> getMyPermissions(User user) {
        return new ArrayList<>(user.getRole().getPermissions());
    }

}
