package com.brokerx.auth_service.infrastructure.security;

import org.springframework.stereotype.Service;

import com.brokerx.auth_service.application.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    /* Constructs a PasswordEncoderAdapter with the given PasswordEncoder dependency. */
    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /* Checks if the raw password matches the encoded password using the underlying PasswordEncoder. */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
