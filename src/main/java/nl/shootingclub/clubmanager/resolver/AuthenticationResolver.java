
package nl.shootingclub.clubmanager.resolver;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.shootingclub.clubmanager.configuration.data.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.data.DefaultRoleAccount;
import nl.shootingclub.clubmanager.configuration.data.HTMLTemplate;
import nl.shootingclub.clubmanager.configuration.data.Language;
import nl.shootingclub.clubmanager.dto.LoginDTO;
import nl.shootingclub.clubmanager.dto.RegisterDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.exceptions.AccountValidationException;
import nl.shootingclub.clubmanager.exceptions.TooManyRequestsException;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.account.AccountRole;
import nl.shootingclub.clubmanager.model.data.DefaultImage;
import nl.shootingclub.clubmanager.model.data.Image;
import nl.shootingclub.clubmanager.repository.AccountRoleRepository;
import nl.shootingclub.clubmanager.repository.DefaultImageRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.security.CustomAuthenticationProvider;
import nl.shootingclub.clubmanager.service.EmailService;
import nl.shootingclub.clubmanager.service.JwtService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;

@Controller
public class AuthenticationResolver {
    private final Cache<String, Bucket> ipBucketCache;

    private final UserService userService;

    private final UserRepository userRepository;

    private final CustomAuthenticationProvider authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService userAuthProvider;

    private final AccountRoleRepository accountRoleRepository;

    private final DefaultImageRepository defaultImageRepository;

    private final EmailService emailService;

    private final HttpServletRequest request;

    public AuthenticationResolver(HttpServletRequest request, UserService userService, UserRepository userRepository, CustomAuthenticationProvider authenticationManager, PasswordEncoder passwordEncoder, JwtService userAuthProvider, AccountRoleRepository accountRoleRepository, DefaultImageRepository defaultImageRepository, EmailService emailService) {
        this.request = request;

        ipBucketCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15)) // Adjust the duration as needed
                .build();
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userAuthProvider = userAuthProvider;
        this.accountRoleRepository = accountRoleRepository;
        this.defaultImageRepository = defaultImageRepository;
        this.emailService = emailService;
    }

    @QueryMapping
    public AuthenticationResolver authenticationQueries() {
        return this;
    }
    @MutationMapping
    public AuthenticationResolver authenticationMutations() {
        return this;
    }



    @SchemaMapping(typeName = "AuthenticationQueries")
    public DefaultBooleanResponseDTO validateToken() {
        Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthenticationResolver::createBucketForIp);
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

    @SchemaMapping(typeName = "AuthenticationMutations")
    public DefaultBooleanResponseDTO login(@Argument @Valid LoginDTO loginRequest) {
        Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthenticationResolver::createBucketForIp);
        if (bucket == null || !bucket.tryConsume(1)) {
            DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
            response.setSuccess(false);
            response.setMessage("too-many-requests");
            return response;
        }
        // Authenticatieproces
        Optional<Authentication> auth = authenticationManager.authenticateOptional(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail().toLowerCase(), loginRequest.getPassword()));
        if(auth.isEmpty()) {
            DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
            response.setSuccess(false);
            response.setMessage("account-bad-credentials");
            return response;
        }
        SecurityContextHolder.getContext().setAuthentication(auth.get());

        // Token creatie
        final String token = userAuthProvider.createToken(new HashMap<>(), (String) auth.get().getPrincipal());

        // Response voorbereiden
        DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
        response.setSuccess(true);
        response.setMessage(token);

        return response;
    }

    @SchemaMapping(typeName = "AuthenticationMutations")
    public DefaultBooleanResponseDTO register( @Argument @Valid RegisterDTO registerRequest) {
        try {
            registerRequest.setEmail(registerRequest.getEmail().toLowerCase());
            Bucket bucket = ipBucketCache.get(request.getRemoteAddr(), AuthenticationResolver::createBucketForIp);
            if (bucket == null || !bucket.tryConsume(1)) {
                DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
                response.setSuccess(false);
                response.setMessage("too-many-requests");
                return response;
            }

            DefaultBooleanResponseDTO response = new DefaultBooleanResponseDTO();
            Optional<User> optionalUser = userRepository.findByEmailEquals(registerRequest.getEmail().toLowerCase());

            if (optionalUser.isPresent()) {
                response.setSuccess(false);
                response.setMessage("email-already-used");

                return response;
            }

            User user = new User();
            user.setFullName(registerRequest.getFullName());
            user.setEmail(registerRequest.getEmail().toLowerCase());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            Optional<Language> optionalLanguage = Language.fromString(registerRequest.getLanguage());
            if(optionalLanguage.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("language-not-found");
                return response;
            }
            user.setLanguage(optionalLanguage.get().getLanguage());

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
                response.setSuccess(false);
                response.setMessage("account-validation-error");

                return response;
            }

            userService.createUser(user);

            Optional<Authentication> auth = authenticationManager.authenticateOptional(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), registerRequest.getPassword()));
            if(auth.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("account-bad-credentials");
                return response;
            }
            SecurityContextHolder.getContext().setAuthentication(auth.get());

            // Token creatie
            final String token = userAuthProvider.createToken(new HashMap<>(), (String) auth.get().getPrincipal());
//            System.out.println("token: " + token);


//            final String token = userAuthProvider.createToken(new HashMap<>(), user.getEmail());

            response.setSuccess(true);
            response.setMessage(token);
            new Thread(() -> {
                try {
                    emailService.sendHTMLMail(user.getEmail(), HTMLTemplate.REGISTERED, optionalLanguage.get(), new HashMap<>());
                } catch (MessagingException e) {
                    System.out.printf("Could not send registered mail to " + user.getEmail());
                }
            }).start();

            return response;

        } catch (AuthenticationException e) {
            throw new AccountValidationException("Could not create account");
        }
    }
}
