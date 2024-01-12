
package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.model.AssociationInvite;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AssociationInviteRepository;
import nl.shootingclub.clubmanager.repository.AssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssociationInviteService {

    @Autowired
    private AssociationInviteRepository associationInviteRepository;

    public List<AssociationInvite> getMyInvites(User user) {
        return associationInviteRepository.findByUser(user);
    }

    public AssociationInvite createAssociationInvite(AssociationInvite associationInvite) {
        return associationInviteRepository.save(associationInvite);
    }
}
