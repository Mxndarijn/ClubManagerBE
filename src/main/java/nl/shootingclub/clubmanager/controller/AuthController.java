package nl.shootingclub.clubmanager.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAccount;
import nl.shootingclub.clubmanager.dto.LoginDTO;
import nl.shootingclub.clubmanager.dto.RegisterDTO;
import nl.shootingclub.clubmanager.exceptions.AccountValidationException;
import nl.shootingclub.clubmanager.exceptions.EmailAlreadyUsedException;
import nl.shootingclub.clubmanager.exceptions.TooManyRequestsException;
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
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
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


    // Handles rate limiting
    private Cache<String, Bucket> ipBucketCache;

    public AuthController() {
        ipBucketCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15)) // Adjust the duration as needed
                .build();
    }

    private static Bucket createBucketForIp(String ip) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build();
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(HttpServletRequest request, @RequestBody @Valid LoginDTO loginRequest) {
        Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthController::createBucketForIp);
        if (bucket == null || !bucket.tryConsume(1)) {
            throw new TooManyRequestsException("too many requests for login");
        }

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword(), new ArrayList<>()
                    ));
            SecurityContextHolder.getContext().setAuthentication(auth);

            final String token = userAuthProvider.createToken(new HashMap<>(), (String) auth.getPrincipal());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", token);

            return ResponseEntity.ok(response);


    }

    @PostMapping("/validateToken")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthController::createBucketForIp);
        if (bucket == null || !bucket.tryConsume(1)) {
            throw new TooManyRequestsException("too many requests for login");
        }

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
    public ResponseEntity<Map<String,Object>> register(HttpServletRequest request, @RequestBody @Valid RegisterDTO registerRequest) {
        try {
            Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthController::createBucketForIp);
            if (bucket == null || !bucket.tryConsume(1)) {
                throw new TooManyRequestsException("too many requests for login");
            }

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

            Optional<AccountRole> optionalAccountRole = accountRoleRepository.findByName(DefaultRoleAccount.USER.getName());
            if(optionalAccountRole.isPresent()) {
                user.setRole(optionalAccountRole.get());
            } else {
                throw new AccountValidationException("Could not create account");
            }

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