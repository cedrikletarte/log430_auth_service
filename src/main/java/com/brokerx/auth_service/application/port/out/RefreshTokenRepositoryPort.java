package com.brokerx.auth_service.application.port.out;

import java.util.Optional;

import com.brokerx.auth_service.domain.model.RefreshToken;
import com.brokerx.auth_service.domain.model.User;

public interface RefreshTokenRepositoryPort {

    /**
     * Store a refresh token
     */
    RefreshToken save(RefreshToken token);

    /**
     * Find a refresh token by its token string
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find a refresh token by user, IP address, user agent, and revoked status
     */
    Optional<RefreshToken> findByUserAndIpAddressAndUserAgentAndRevoked(User user, String ip, String ua,
            boolean revoked);
}
