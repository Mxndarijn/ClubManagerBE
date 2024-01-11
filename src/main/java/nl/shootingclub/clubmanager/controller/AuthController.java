package nl.shootingclub.clubmanager.controller;

import jakarta.validation.Valid;
import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRole;
import nl.shootingclub.clubmanager.dto.LoginDTO;
import nl.shootingclub.clubmanager.dto.RegisterDTO;
import nl.shootingclub.clubmanager.exceptions.AccountValidationException;
import nl.shootingclub.clubmanager.exceptions.EmailAlreadyUsedException;
import nl.shootingclub.clubmanager.model.AccountRole;
import nl.shootingclub.clubmanager.model.DefaultImage;
import nl.shootingclub.clubmanager.model.Image;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.repository.AccountRoleRepository;
import nl.shootingclub.clubmanager.repository.DefaultImageRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.JwtService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private JwtService userAuthProvider;

    @Autowired
    private AccountRoleRepository accountRoleRepository;

    @Autowired
    private DefaultImageRepository defaultImageRepository;


    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody @Valid LoginDTO loginRequest) {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword(), new ArrayList<>()
                    ));
            SecurityContextHolder.getContext().setAuthentication(auth);

            Optional<User> optionalUser = userRepository.findByEmailEquals(loginRequest.getEmail());

            if(optionalUser.isEmpty()) {
                throw new BadCredentialsException("credentials wrong");
            }

            final String token = userAuthProvider.createToken(new HashMap<>(), optionalUser.get().getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", token);

            return ResponseEntity.ok(response);


    }

    @PostMapping("/validateToken")
    public ResponseEntity<Map<String, Object>> validateToken() {

        if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "token is not valid");

            return ResponseEntity.ok(response);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "token is correct");

        return ResponseEntity.ok(response);
    }

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

            Optional<DefaultImage> image = defaultImageRepository.findByName(DefaultImageData.PROFILE_PICTURE.getName());
            if(image.isPresent()) {
                Image i = new Image();
                i.setEncoded(image.get().getImage().getEncoded());
                user.setImage(i);

            }

            userService.createUser(user);

            Optional<AccountRole> optionalAccountRole = accountRoleRepository.findByName(DefaultRole.USER.getName());
            if(optionalAccountRole.isPresent()) {
                user.getRoles().add(optionalAccountRole.get());
                userRepository.save(user);
            }


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