package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return  userRepository.findAll();
    }


    public boolean authenticate(String name, String password) {
        Optional<User> user = userRepository.findByUserNameEquals(name);
        if (user.isPresent()) {
            // Assuming the password field is present in the User model and is called password
            return user.get().getPassword().equals(password);
        }
        return false;
    }
}