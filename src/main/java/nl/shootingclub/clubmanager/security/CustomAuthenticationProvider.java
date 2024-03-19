package nl.shootingclub.clubmanager.security;

import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (userService.authenticate(email, password)) {
            return authentication;
        } else {
            return null;
        }
    }
    public Optional<Authentication> authenticateOptional(Authentication authentication) {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (userService.authenticate(email, password)) {
            return Optional.of(authentication);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}