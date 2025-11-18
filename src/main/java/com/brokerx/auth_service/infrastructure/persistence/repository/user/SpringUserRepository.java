package com.brokerx.auth_service.infrastructure.persistence.repository.user;

import com.brokerx.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringUserRepository extends JpaRepository<UserEntity, Long> {

    /* Finds a user entity by its email address. */
    Optional<UserEntity> findByEmail(String email);

    /* Checks if a user entity exists with the given email address. */
    boolean existsByEmail(String email);
}
