package com.brokerx.auth_service.application.port.out;

import java.util.Optional;

import com.brokerx.auth_service.domain.model.User;

public interface UserRepositoryPort {

    /**
     * Store a user
     */
    User save(User user);

    /**
     * Find a user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists by email
     */
    boolean existsByEmail(String email);
}