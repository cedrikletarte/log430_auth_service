package com.brokerx.auth_service.infrastructure.config;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.brokerx.auth_service.infrastructure.persistence.repository.user.UserRepositoryAdapter;
import com.brokerx.auth_service.infrastructure.security.UserDetailsAdapter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepositoryAdapter userRepositoryAdapter;

    /**
     * Configures the user details service to load user information by email for authentication.
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepositoryAdapter.findByEmail(username)
                .map(UserDetailsAdapter::new)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Provides a BCrypt password encoder bean for secure password hashing.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a secure random number generator bean for cryptographic operations.
     */
    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
