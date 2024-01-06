package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData;
import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.AccountRole;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AccountPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PermissionService {

    @Autowired
    private AccountPermissionRepository accountPermissionRepository;

    public boolean validatePermission(AccountPermissionData accountPermissionData) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<AccountPermission> perm = accountPermissionRepository.findByNameEquals(accountPermissionData.getName());
            if(perm.isEmpty())
                return false;

            for (AccountRole role : user.getRoles()) {
                if (role.getPermissions().contains(perm.get())) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
