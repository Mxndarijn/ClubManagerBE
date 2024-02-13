package nl.shootingclub.clubmanager.configuration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {


    private final String ipAddress;
    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, String ipadress) {
        super(principal, credentials);
        this.ipAddress = ipadress;
    }

    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String ipadress) {
        super(principal, credentials, authorities);
        this.ipAddress = ipadress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
