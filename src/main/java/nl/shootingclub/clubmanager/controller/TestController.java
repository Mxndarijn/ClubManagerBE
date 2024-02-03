package nl.shootingclub.clubmanager.controller;

import jakarta.validation.Valid;
import nl.shootingclub.clubmanager.dto.LoginDTO;
import nl.shootingclub.clubmanager.dto.RegisterDTO;
import nl.shootingclub.clubmanager.exceptions.AccountNotFoundException;
import nl.shootingclub.clubmanager.exceptions.AccountValidationException;
import nl.shootingclub.clubmanager.exceptions.EmailAlreadyUsedException;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.JwtService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService userAuthProvider;


    /**
     * Performs the login operation and return a response entity with a map containing the success status.
     *
     * @return A response entity containing a map with "success" key set to true.
     */
    @PostMapping("/test2")
    public ResponseEntity<Map<String,Object>> login() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
            return ResponseEntity.ok(response);
    }

    /**
     * Registers a new user.
     *
     * @param registerRequest The registration request body containing the user's email, password, and full name.
     * @return A ResponseEntity with a success flag and a token message.
     * @throws EmailAlreadyUsedException If the provided email is already associated with an existing account.
     * @throws AccountValidationException If an error occurs during account creation.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@RequestBody @Valid RegisterDTO registerRequest) {
        try {

            Optional<User> optionalUser = userRepository.findByEmailEquals(registerRequest.getEmail().toLowerCase());

            if(optionalUser.isPresent()) {
                throw new EmailAlreadyUsedException("Email already has an account");
            }

            User user = new User();
            user.setFullName(registerRequest.getFullName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            userService.createUser(user);


            final String token = userAuthProvider.createToken(new HashMap<>(), user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", token);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            throw new AccountValidationException("Could not create account");
        }
    }


}