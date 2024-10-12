package nl.shootingclub.clubmanager.repository;

import io.micrometer.observation.annotation.Observed;
import nl.shootingclub.clubmanager.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Cacheable(value = "findByEmailEquals", key = "#email")
    @Observed
    Optional<User> findByEmailEquals(String email);
}