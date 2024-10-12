package nl.shootingclub.clubmanager.helper;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

    private final AssociationRepository associationRepository;

    public UserHelper(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @Observed
    @Cacheable(value = "doUsersHaveSharedAssociation", key = "#user.id + '-' + #user2.id")
    public boolean doUsersHaveSharedAssociation(User user, User user2) {
        return !associationRepository.findSharedAssociations(user.getId(), user2.getId()).isEmpty();
    }
}
