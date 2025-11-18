package com.brokerx.auth_service.infrastructure.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.brokerx.auth_service.application.port.out.AuthenticatorPort;

@Service
public class SpringSecurityAuthenticator implements AuthenticatorPort {
    private final AuthenticationManager authenticationManager;

    /* Constructs a SpringSecurityAuthenticator with the Spring Security authentication manager. */
    public SpringSecurityAuthenticator(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /* Authenticates a user using Spring Security's authentication manager with email and password. */
    @Override
    public void authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
    }
}
