package nl.shootingclub.clubmanager.service;

import kotlin.collections.ArrayDeque;
import nl.shootingclub.clubmanager.configuration.UserAuthProvider;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {

    private final AssociationRepository associationRepository;

    private final UserAuthProvider userAuthProvider;

    public AssociationService(AssociationRepository associationrepository, UserAuthProvider userAuthProvider) {
        this.associationRepository = associationrepository;
        this.userAuthProvider = userAuthProvider;
    }

    public List<Association> getMyAssociations() {
        Optional<User> optionalUser = userAuthProvider.getUserFromAuthentication();
        if(optionalUser.isEmpty()) {
            return new ArrayList<>();
        }
        List<Association> list = associationRepository.findByUser(optionalUser.get());
        list.forEach(a -> {
            a.setAssociationUserRoles(null);
            a.setUsers(null);
        });

        return list;
    }

    public Association createAssociation(Association association) {
        return associationRepository.save(association);
    }
}
