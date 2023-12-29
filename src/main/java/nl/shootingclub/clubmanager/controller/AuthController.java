package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.configuration.UserAuthProvider;
import nl.shootingclub.clubmanager.dto.LoginDTO;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAuthProvider userAuthProvider;


    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginDTO loginRequest) {
        try {
            System.out.println("LoginRequest" + LocalDateTime.now().toString());
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    ));

            SecurityContextHolder.getContext().setAuthentication(auth);

            Optional<User> optionalUser = userRepository.findByEmailEquals(loginRequest.getEmail());

            if(optionalUser.isEmpty()) {
                return getNotLoggedIn();
            }

            final String token = userAuthProvider.createToken(optionalUser.get());
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("loggedIn", "true");
            return new ResponseEntity<>(tokenMap, HttpStatus.OK);

        } catch (AuthenticationException e) {
            return getNotLoggedIn();
        }
    }

    private ResponseEntity<Map<String,String>> getNotLoggedIn() {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("loggedIn", "false");
        return new ResponseEntity<>(tokenMap, HttpStatus.OK);
    }


}