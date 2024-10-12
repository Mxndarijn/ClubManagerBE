package nl.shootingclub.clubmanager.repository;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.model.UserAssociationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAssociationRepository extends JpaRepository<UserAssociation, UserAssociationId> {

    @Observed
//    @Cacheable(value = "userAssociations", key = "#userId + '-' + #associationId")
    Optional<UserAssociation> findByUserIdAndAssociationId(UUID userId, UUID associationId);

    @Observed
//    @Cacheable(value = "userAssociations", key = "#id")
    List<UserAssociation> findByUserId(UUID id);


}