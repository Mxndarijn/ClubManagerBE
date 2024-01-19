
package nl.shootingclub.clubmanager.service;

import kotlin.collections.ArrayDeque;
import nl.shootingclub.clubmanager.UserInfoDetails;
import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    public List<Association> getMyAssociations(User user) {
        return associationRepository.findByUser(user);
    }

    public Association createAssociation(Association association) {
        return associationRepository.save(association);
    }

    public Optional<Association> getByID(UUID associationUUID) {
        return associationRepository.findById(associationUUID);
    }

    public void saveAssociation(Association association) {
        associationRepository.save(association);
    }
}
