package com.brokerx.auth_service.application.port.out;

import java.util.List;
import java.util.Optional;

import com.brokerx.auth_service.domain.model.User;

public interface UserRepositoryPort {

    /** Store a user */
    User save(User user);

    /** Store multiple users */
    List<User> saveAll(List<User> users);

    /** Find a user by id */
    Optional<User> findById(Long id);

    /** Find all users */
    List<User> findAll();

    /** Find a user by email */
    Optional<User> findByEmail(String email);

    /** Check if a user exists by email */
    boolean existsByEmail(String email);

    /** Check if a user exists by id */
    boolean existsById(Long id);

    /** Delete a user by id */
    void deleteById(Long id);

    /** Delete all users */
    void deleteAll();
}