package nl.shootingclub.clubmanager.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.schema.DataFetchingEnvironment;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.shootingclub.clubmanager.configuration.data.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.data.DefaultRoleAccount;
import nl.shootingclub.clubmanager.dto.LoginDTO;
import nl.shootingclub.clubmanager.dto.RegisterDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
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
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
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

    /**
     * AuthController handles authentication-related operations such as login, registration, and token validation.
     */
    public AuthController() {
        ipBucketCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15)) // Adjust the duration as needed
                .build();
    }

    /**
     * Creates a bucket for the given IP address.
     *
     * @param ip The IP address for which to create the bucket.
     * @return The created Bucket.
     */
    private static Bucket createBucketForIp(String ip) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build();
    }

    @Autowired
    private HttpServletRequest request;

    @MutationMapping
    public DefaultBooleanResponseDTO login(@Argument @Valid LoginDTO loginRequest) {
        Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthController::createBucketForIp);
        if (bucket == null || !bucket.tryConsume(1)) {
            throw new TooManyRequestsException("Too many requests for login");
        }

        // Authenticatieproces
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword(), new ArrayList<>()
                ));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Token creatie
        final String token = userAuthProvider.createToken(new HashMap<>(), (String) auth.getPrincipal());

        // Response voorbereiden
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        response.setSuccess(true);
        response.setMessage(token);

        return response;
    }


    @QueryMapping
    public DefaultBooleanResponseDTO validateToken() {
        Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthController::createBucketForIp);
        if (bucket == null || !bucket.tryConsume(1)) {
            throw new TooManyRequestsException("too many requests for login");
        }
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();

        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)) {
            response.setSuccess(false);
            response.setMessage("token-invalid");

            return response;
        }
        response.setSuccess(true);
        response.setMessage("token-valid");
        return response;
    }

    @MutationMapping
    public DefaultBooleanResponseDTO register( @Argument @Valid RegisterDTO registerRequest) {
        try {
            Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthController::createBucketForIp);
            if (bucket == null || !bucket.tryConsume(1)) {
                throw new TooManyRequestsException("too many requests for login");
            }

            DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
            Optional<User> optionalUser = userRepository.findByEmailEquals(registerRequest.getEmail().toLowerCase());

            if (optionalUser.isPresent()) {
                throw new EmailAlreadyUsedException("Email already has an account");
            }

            User user = new User();
            user.setFullName(registerRequest.getFullName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            Optional<DefaultImage> image = defaultImageRepository.findByName(DefaultImageData.PROFILE_PICTURE.getName());
            if (image.isPresent()) {
                Image i = new Image();
                i.setEncoded(image.get().getImage().getEncoded());
                user.setImage(i);
            }

            Optional<AccountRole> optionalAccountRole = accountRoleRepository.findByName(DefaultRoleAccount.USER.getName());
            if (optionalAccountRole.isPresent()) {
                user.setRole(optionalAccountRole.get());
            } else {
                throw new AccountValidationException("Could not create account");
            }

            userService.createUser(user);


            final String token = userAuthProvider.createToken(new HashMap<>(), user.getEmail());

            response.setSuccess(true);
            response.setMessage(token);

            return response;

        } catch (AuthenticationException e) {
            throw new AccountValidationException("Could not create account");
        }
    }


}