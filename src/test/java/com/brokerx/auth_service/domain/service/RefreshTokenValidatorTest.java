package com.brokerx.auth_service.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.model.RefreshToken;
import com.brokerx.auth_service.domain.model.User;

class RefreshTokenValidatorTest {

    private String generateValidToken() {
        // Génère un token base64url valide de longueur suffisante
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    }

    private RefreshToken createValidRefreshToken() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        RefreshToken token = RefreshToken.builder()
                .token(generateValidToken())
                .user(user)
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plusSeconds(86400))
                .build();
        token.setRevoked(false);
        return token;
    }

    @Test
    void shouldValidateRefreshTokenForCreation() {
        RefreshToken refreshToken = createValidRefreshToken();

        assertDoesNotThrow(() -> RefreshTokenValidator.validateForCreation(refreshToken));
    }

    @Test
    void shouldRejectNullRefreshToken() {
        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(null));
        assertTrue(exception.getMessage().contains("Refresh token object is required"));
    }

    @Test
    void shouldRejectRefreshTokenWithoutUser() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setUser(null);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("User is required"));
    }

    @Test
    void shouldRejectRefreshTokenWithoutUserId() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.getUser().setId(null);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("User ID is required"));
    }

    @Test
    void shouldRejectNullToken() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setToken(null);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("token is required"));
    }

    @Test
    void shouldRejectEmptyToken() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setToken("   ");

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("token is required"));
    }

    @Test
    void shouldRejectTooShortToken() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setToken("short");

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("token too short"));
    }

    @Test
    void shouldRejectTokenWithInvalidCharacters() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setToken("Invalid@Token!WithSpecial#Characters$" + "x".repeat(60));

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("must be base64url"));
    }

    @Test
    void shouldAcceptValidBase64UrlToken() {
        RefreshToken refreshToken = createValidRefreshToken();
        
        // Token avec caractères base64url valides
        refreshToken.setToken("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_" + "a".repeat(30));
        assertDoesNotThrow(() -> RefreshTokenValidator.validateForCreation(refreshToken));
        
        // Token avec padding
        refreshToken.setToken("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_" + "a".repeat(30) + "==");
        assertDoesNotThrow(() -> RefreshTokenValidator.validateForCreation(refreshToken));
    }

    @Test
    void shouldRejectNullExpiryDate() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setExpiryDate(null);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("Expiry date is required"));
    }

    @Test
    void shouldRejectPastExpiryDate() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setExpiryDate(Instant.now().minusSeconds(3600));

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("Expiry date must be in the future"));
    }

    @Test
    void shouldRejectNullCreatedAt() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setCreatedAt(null);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("Creation date is required"));
    }

    @Test
    void shouldRejectCreatedAtAfterExpiryDate() {
        RefreshToken refreshToken = createValidRefreshToken();
        Instant now = Instant.now();
        refreshToken.setCreatedAt(now.plusSeconds(7200));
        refreshToken.setExpiryDate(now.plusSeconds(3600));

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("Creation date cannot be after expiry date"));
    }

    @Test
    void shouldRejectRevokedTokenOnCreation() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setRevoked(true);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForCreation(refreshToken));
        assertTrue(exception.getMessage().contains("Revoked must be false on creation"));
    }

    @Test
    void shouldValidateRefreshTokenForRevocation() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setRevoked(true);

        assertDoesNotThrow(() -> RefreshTokenValidator.validateForRevocation(refreshToken, false));
    }

    @Test
    void shouldValidateRefreshTokenForRevocationWithReplacement() {
        RefreshToken newToken = createValidRefreshToken();
        RefreshToken oldToken = createValidRefreshToken();
        oldToken.setRevoked(true);
        oldToken.setReplacedBy(newToken);

        assertDoesNotThrow(() -> RefreshTokenValidator.validateForRevocation(oldToken, true));
    }

    @Test
    void shouldRejectRevocationOfNonRevokedToken() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setRevoked(false);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForRevocation(refreshToken, false));
        assertTrue(exception.getMessage().contains("Revoked must be true when revoking"));
    }

    @Test
    void shouldRejectRevocationWithoutReplacementWhenRequired() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setRevoked(true);
        refreshToken.setReplacedBy(null);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> RefreshTokenValidator.validateForRevocation(refreshToken, true));
        assertTrue(exception.getMessage().contains("ReplacedBy must reference the new token when revoking"));
    }

    @Test
    void shouldAllowRevocationWithoutReplacementWhenNotRequired() {
        RefreshToken refreshToken = createValidRefreshToken();
        refreshToken.setRevoked(true);
        refreshToken.setReplacedBy(null);

        // Pour logout manuel, le remplacement n'est pas requis
        assertDoesNotThrow(() -> RefreshTokenValidator.validateForRevocation(refreshToken, false));
    }
}
