package nl.shootingclub.clubmanager.helper;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserHelper {

    private final UserAssociationRepository userAssociationRepository;

    public UserHelper(UserAssociationRepository userAssociationRepository) {
        this.userAssociationRepository = userAssociationRepository;
    }

    public boolean doUsersHaveSharedAssociation(User user, User user2) {
        List<UserAssociation> userUserAssociationList = userAssociationRepository.findByUserId(user.getId());
        List<UserAssociation> contextUserUserAssociationList = userAssociationRepository.findByUserId(user2.getId());

        boolean hasSharedAssociation = userUserAssociationList.stream()
                .map(UserAssociation::getAssociation)
                .anyMatch(association -> contextUserUserAssociationList.stream()
                        .map(UserAssociation::getAssociation)
                        .anyMatch(userAssociation -> userAssociation.getId().equals(association.getId()))
                );
        return hasSharedAssociation;
    }
}
