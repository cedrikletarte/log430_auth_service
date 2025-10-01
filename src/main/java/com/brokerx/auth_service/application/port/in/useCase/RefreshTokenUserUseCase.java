package com.brokerx.auth_service.application.port.in.useCase;

import java.util.Optional;

import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.refresh.RefreshSuccess;
import com.brokerx.auth_service.domain.model.RefreshToken;

public interface RefreshTokenUserUseCase {
    RefreshSuccess createRefreshToken(String email, String ipAddress, String userAgent);
    LoginSuccess refreshToken(String refreshToken);
    void revoke(RefreshToken token, RefreshToken replacedBy);
    Optional<RefreshToken> findByToken(String token);
    void verifyExpiration(RefreshToken token);
}
