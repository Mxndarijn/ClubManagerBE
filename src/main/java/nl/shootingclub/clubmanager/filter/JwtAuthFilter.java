package nl.shootingclub.clubmanager.filter;

import io.jsonwebtoken.JwtException;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.shootingclub.clubmanager.configuration.CustomUsernamePasswordAuthenticationToken;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.service.JwtService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userDetailsService;


    @Autowired
    private Tracer tracer;

    public JwtAuthFilter(JwtService jwtService, UserService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
//    @Observed
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        Span span = tracer.nextSpan().name("doFilterInternal").start();
        if(header != null) {
            String[] authElements = header.split(" ");

            if(authElements.length == 2 && "Bearer".equals(authElements[0])) {
                String token = authElements[1];
                try {
                    String email = jwtService.extractUsername(token);
                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        Optional<User> userDetails = userDetailsService.loadUserByEmail(email);
                        if (userDetails.isPresent()) {
                            if (jwtService.validateToken(token, userDetails.get())) {
                                CustomUsernamePasswordAuthenticationToken authToken = new CustomUsernamePasswordAuthenticationToken(userDetails.get(), null, request.getRemoteAddr());
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }
                        }
                    }
                } catch (JwtException ignored) {

                    // continue without auth, will be denied eventually, if the user needs to be authenticated
                }

            }
        }
        span.end();
        filterChain.doFilter(request, response);
    }
}