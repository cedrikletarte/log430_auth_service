package com.brokerx.auth_service.application.port.in.useCase;

import java.util.Optional;

import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.refresh.RefreshSuccess;
import com.brokerx.auth_service.domain.model.RefreshToken;

public interface RefreshTokenUserUseCase {

    /* Creates a new refresh token for the user identified by email. */
    RefreshSuccess createRefreshToken(String email, String ipAddress, String userAgent);

    /* Refreshes the access token using the provided refresh token. */
    LoginSuccess refreshToken(String refreshToken);

    /* Revokes the provided refresh token, optionally replacing it with another token. */
    void revoke(RefreshToken token, RefreshToken replacedBy);

    /* Finds a refresh token by its token string. */
    Optional<RefreshToken> findByToken(String token);

    /* Verifies if the provided refresh token has expired. */
    void verifyExpiration(RefreshToken token);
}
