package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.UserInfoDetails;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return  userRepository.findAll();
    }


    public boolean authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmailEquals(email);
        if (user.isPresent()) {
            return encoder.matches(password, user.get().getPassword());
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmailEquals(email);
        if(optionalUser.isPresent()) {
            return new UserInfoDetails(optionalUser.get());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Optional<User> loadUserByEmail(String email) {
        return userRepository.findByEmailEquals(email);
    }

    public Optional<User> getUser(User user) {
        return userRepository.findById(user.getId());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}