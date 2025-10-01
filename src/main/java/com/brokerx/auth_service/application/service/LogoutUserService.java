package com.brokerx.auth_service.application.service;

import org.springframework.stereotype.Service;

import com.brokerx.auth_service.application.port.in.useCase.LogoutUserUseCase;
import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.model.RefreshToken;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutUserService implements LogoutUserUseCase {

    private final RefreshTokenService refreshTokenService;

    /**
     * Logs out a user by revoking their refresh token if it exists and is valid.
     */
    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            // If no refresh token is provided, there's nothing to revoke
            return;
        }

        // Find the refresh token in the database
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> RefreshTokenException.notFound(refreshToken));

        // Revoke the token (mark it as revoked)
        refreshTokenService.revoke(token, null);
    }
}