package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAssociationService {

    private final UserAssociationRepository userAssociationRepository;

    public UserAssociationService(UserAssociationRepository repo) {
        this.userAssociationRepository = repo;
    }


    public void createUserAssociation(UserAssociation userAssociation) {
        userAssociationRepository.save(userAssociation);
    }

    public void saveUserAssociation(UserAssociation userAssociation) {
        userAssociationRepository.save(userAssociation);
    }

    public void deleteUserAssociation(UserAssociation userAssociation) {
        userAssociationRepository.delete(userAssociation);
    }
}
