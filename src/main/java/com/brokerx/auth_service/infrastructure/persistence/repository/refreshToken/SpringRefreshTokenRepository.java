package com.brokerx.auth_service.infrastructure.persistence.repository.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brokerx.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;
import com.brokerx.auth_service.infrastructure.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface SpringRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUserAndIpAddressAndUserAgentAndRevoked(UserEntity user, String ipAddress,
            String userAgent, boolean revoked);

    List<RefreshTokenEntity> findByUser(UserEntity user);
}
