package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.service.JwtService;
import nl.shootingclub.clubmanager.service.PermissionService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Permission;

@Configuration
public class AppConfig {

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }


    @Bean
    public PermissionService permissionService() {
            return new PermissionService();
    }
    @Bean
    public UserService userDetailsService() {
        return new UserService();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
