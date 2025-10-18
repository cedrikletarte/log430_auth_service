package com.brokerx.auth_service.application.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.refresh.RefreshSuccess;
import com.brokerx.auth_service.application.port.in.useCase.RefreshTokenUserUseCase;
import com.brokerx.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.brokerx.auth_service.application.port.out.UserRepositoryPort;

import jakarta.transaction.Transactional;
import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.model.RefreshToken;
import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserStatus;
import com.brokerx.auth_service.domain.service.RefreshTokenValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUserUseCase {

    private static final Logger logger = LogManager.getLogger(RefreshTokenService.class);

    @Value("${refresh.token.expiration.days}")
    private long refreshTokenDurationDays;

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final JwtService jwtService;
    private final SecureRandom secureRandom;

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /**
     * Creates a new refresh token for the user, revoking any existing token from the same device.
     */
    @Transactional
    public RefreshSuccess createRefreshToken(String email, String ipAddress, String userAgent) {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        User user = userRepository.findByEmail(email.toLowerCase()).orElseThrow();

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(base64Encoder.encodeToString(randomBytes))
                .expiryDate(LocalDateTime.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS))
                .isRevoked(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();

        RefreshTokenValidator.validateForCreation(rt);

        var existing = refreshTokenRepository.findByUserAndIpAddressAndUserAgentAndRevoked(user, ipAddress, userAgent,
                false);

        // First persist the new token to avoid transient reference issues when linking
        var savedRt = refreshTokenRepository.save(rt);
        existing.ifPresent(e -> revoke(e, savedRt));

        return RefreshSuccess.builder()
                .refreshToken(savedRt.getToken())
                .expiryDate(savedRt.getExpiryDate())
                .build();
    }

    /**
     * Validates a refresh token and generates a new access token for the authenticated user.
     */
    @Transactional
    public LoginSuccess refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Refresh token not found or invalid");
                    return RefreshTokenException.notFound(refreshToken);
                });

        User user = token.getUser();

        if (user.getStatus().equals(UserStatus.SUSPENDED)) {
            logger.warn("Refresh token denied - User suspended: {}", user.getEmail());
            throw UserException.notActive(user.getId(), user.getStatus().name());
        }

        String accessToken = jwtService.generateToken(user);
        
        logger.info("Token refreshed successfully for user: {}", user.getEmail());

        return LoginSuccess.builder()
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .accessToken(accessToken)
                .build();
    }

    /**
     * Revokes a refresh token and optionally links it to the replacement token.
     * For manual revocations (logout), replacedBy can be null.
     * For token rotation, replacedBy should reference the new token.
     */
    @Transactional
    public void revoke(RefreshToken token, RefreshToken replacedBy) {
        token.setRevoked(true);
        token.setReplacedBy(replacedBy);
        RefreshTokenValidator.validateForRevocation(token, replacedBy != null);
        refreshTokenRepository.save(token);
    }

    /**
     * Finds a refresh token by its token string value.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies that a refresh token is not expired or revoked, throwing exceptions if invalid.
     */
    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw RefreshTokenException.expired(token.getToken(), token.getExpiryDate());
        }
        if (token.isRevoked()) {
            throw RefreshTokenException.revoked(token.getToken());
        }
    }
}
