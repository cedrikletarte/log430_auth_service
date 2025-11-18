package com.brokerx.auth_service.infrastructure.persistence.repository.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brokerx.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;
import com.brokerx.auth_service.infrastructure.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface SpringRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /* Finds a refresh token entity by its token string. */
    Optional<RefreshTokenEntity> findByToken(String token);

    /* Finds a refresh token entity for a specific user, IP address, user agent, and revocation status. */
    Optional<RefreshTokenEntity> findByUserAndIpAddressAndUserAgentAndRevoked(UserEntity user, String ipAddress,
            String userAgent, boolean revoked);

    /* Finds all refresh token entities associated with a specific user. */
    List<RefreshTokenEntity> findByUser(UserEntity user);
}
