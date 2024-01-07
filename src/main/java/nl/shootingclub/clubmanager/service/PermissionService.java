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

import java.util.Optional;

@Component
public class PermissionService {

    @Autowired
    private AccountPermissionRepository accountPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean validatePermission(AccountPermissionData accountPermissionData) {
        try {
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            User tempUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<User> optionalUser = userRepository.findById(tempUser.getId());
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                System.out.println(user);
                Optional<AccountPermission> perm = accountPermissionRepository.findByNameEquals(accountPermissionData.getName());
                if(perm.isEmpty()) {
                    System.out.println("Permission not found");
                    return false;
                }

                for (AccountRole role : user.getRoles()) {
                    if (role.getPermissions().contains(perm.get())) {
                        System.out.println("Permission found");
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
}
