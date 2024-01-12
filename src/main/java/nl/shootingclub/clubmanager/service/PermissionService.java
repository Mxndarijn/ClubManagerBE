package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData;
import nl.shootingclub.clubmanager.model.AccountPermission;
import nl.shootingclub.clubmanager.model.AccountRole;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AccountPermissionRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PermissionService {

    @Autowired
    private AccountPermissionRepository accountPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean validatePermission(AccountPermissionData accountPermissionData) {
        try {
            if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)) {
                return false;
            }
            User tempUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<User> optionalUser = userRepository.findById(tempUser.getId());
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                Optional<AccountPermission> perm = accountPermissionRepository.findByNameEquals(accountPermissionData.getName());
                if(perm.isEmpty()) {
                    System.out.println("Permission not found");
                    return false;
                }

                for (AccountRole role : user.getRoles()) {
                    if (role.getPermissions().contains(perm.get())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public List<AccountPermission> getMyPermissions(User user) {
        return new ArrayList<>(user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .toList());
    }

}
