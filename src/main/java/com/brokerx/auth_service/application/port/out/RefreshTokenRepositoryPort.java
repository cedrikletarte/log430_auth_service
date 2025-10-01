package com.brokerx.auth_service.application.port.out;

import java.util.Optional;

import com.brokerx.auth_service.domain.model.RefreshToken;
import com.brokerx.auth_service.domain.model.User;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserAndIpAddressAndUserAgentAndRevoked(User user, String ip, String ua,
            boolean revoked);
}
