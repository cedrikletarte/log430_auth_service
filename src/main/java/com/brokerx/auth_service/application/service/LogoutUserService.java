package com.brokerx.auth_service.application.service;

import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.brokerx.auth_service.application.port.in.useCase.LogoutUserUseCase;
import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.model.RefreshToken;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutUserService implements LogoutUserUseCase {

    private static final Logger logger = LogManager.getLogger(LogoutUserService.class);

    private final RefreshTokenService refreshTokenService;

    /**
     * Logs out a user by revoking their refresh token if it exists and is valid.
     */
    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            logger.info("Logout called with no refresh token provided");
            return;
        }

        logger.info("Logout attempt - revoking refresh token");

        // Find the refresh token in the database
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Logout failed - Refresh token not found");
                    return RefreshTokenException.notFound(refreshToken);
                });

        // Revoke the token (mark it as revoked)
        refreshTokenService.revoke(token, null);
        
        logger.info("Logout successful - User: {}", token.getUser().getEmail());
    }
}