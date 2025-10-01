package com.brokerx.auth_service.application.port.out;

import java.util.Optional;

import com.brokerx.auth_service.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}