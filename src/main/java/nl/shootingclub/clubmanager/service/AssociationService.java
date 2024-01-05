package nl.shootingclub.clubmanager.service;

import kotlin.collections.ArrayDeque;
import nl.shootingclub.clubmanager.UserInfoDetails;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Association> getMyAssociations() {
        UserInfoDetails userInfoDetails = (UserInfoDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser =  userRepository.findByEmailEquals(userInfoDetails.getUsername());
        System.out.println("info: " + userInfoDetails);
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
