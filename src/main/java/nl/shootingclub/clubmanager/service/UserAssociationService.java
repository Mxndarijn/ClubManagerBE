package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.AssociationRole;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public UserAssociation createUserAssociation(User user, Association association, AssociationRole associationRole) {
        UserAssociation userAssociation = new UserAssociation();
        userAssociation.setUser(user);
        userAssociation.setAssociation(association);
        userAssociation.setAssociationRole(associationRole);
        userAssociation.createID();
        userAssociation.setMemberSince(LocalDateTime.now());
        return userAssociationRepository.save(userAssociation);
    }
}
