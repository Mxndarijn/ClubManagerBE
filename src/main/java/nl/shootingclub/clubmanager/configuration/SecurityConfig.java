package nl.shootingclub.clubmanager.configuration;

import nl.shootingclub.clubmanager.filter.JwtAuthFilter;
import nl.shootingclub.clubmanager.service.JwtService;
import nl.shootingclub.clubmanager.security.CustomAuthenticationProvider;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Returns an instance of AuthenticationManager.
     *
     * @param http The HttpSecurity object to obtain the AuthenticationManagerBuilder from.
     * @return An instance of AuthenticationManager.
     * @throws Exception if an error occurs while building the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    /**
     * Creates a {@link SecurityFilterChain} for the given {@link HttpSecurity} object.
     * Disables CSRF protection, configures CORS, disables HTTP basic authentication.
     * Permits all requests to "/", "/auth/**", and "/graphql", and permits all other requests.
     * Adds the {@link JwtAuthFilter} before the {@link UsernamePasswordAuthenticationFilter}.
     *
     * @param http the {@link HttpSecurity} object to configure the security filter chain for
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
                })
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requestCustomizer -> {
                requestCustomizer
                    .requestMatchers("/","/auth/**", "/graphql").permitAll()
                    .anyRequest().permitAll();
             })
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a {@link CorsConfigurationSource} bean that allows Cross-Origin Resource Sharing (CORS) for all origins, headers, and methods.
     *
     * @return The {@link CorsConfigurationSource} bean.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(false);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}