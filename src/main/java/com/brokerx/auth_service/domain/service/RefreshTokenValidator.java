package com.brokerx.auth_service.domain.service;

import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.model.RefreshToken;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@UtilityClass
public class RefreshTokenValidator {
    private static final Pattern BASE64URL_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+={0,2}$");
    private static final int MIN_TOKEN_LENGTH = 80; // 64 bytes -> typically 88 chars with padding

    /**
     * Validates all required fields and business rules for creating a new refresh token.
     */
    public static void validateForCreation(RefreshToken refreshToken) {
        if (refreshToken == null) {
            throw RefreshTokenException.invalidData("refreshToken", "Refresh token object is required");
        }

        if (refreshToken.getUser() == null) {
            throw RefreshTokenException.invalidData("user", "User is required");
        }
        if (refreshToken.getUser().getId() == null) {
            throw RefreshTokenException.invalidData("user.id", "User ID is required");
        }

        String token = refreshToken.getToken();
        if (token == null || token.trim().isEmpty()) {
            throw RefreshTokenException.invalidFormat("token is required");
        }
        if (token.length() < MIN_TOKEN_LENGTH) {
            throw RefreshTokenException.invalidFormat("token too short");
        }
        if (!BASE64URL_PATTERN.matcher(token).matches()) {
            throw RefreshTokenException.invalidFormat("must be base64url (A-Za-z0-9_- with optional '=' padding)");
        }

        LocalDateTime now = LocalDateTime.now();
        if (refreshToken.getExpiryDate() == null) {
            throw RefreshTokenException.invalidData("expiryDate", "Expiry date is required");
        }
        if (!refreshToken.getExpiryDate().isAfter(now)) {
            throw RefreshTokenException.invalidData("expiryDate", "Expiry date must be in the future");
        }

        if (refreshToken.getCreatedAt() == null) {
            throw RefreshTokenException.invalidData("createdAt", "Creation date is required");
        }
        if (refreshToken.getCreatedAt().isAfter(refreshToken.getExpiryDate())) {
            throw RefreshTokenException.invalidData("createdAt", "Creation date cannot be after expiry date");
        }

        if (refreshToken.isRevoked()) {
            throw RefreshTokenException.invalidData("revoked", "Revoked must be false on creation");
        }
    }

    /**
     * Validates that a refresh token is properly configured for revocation.
     * ReplacedBy can be null for manual revocations (e.g., logout) but must be set for token rotation.
     */
    public static void validateForRevocation(RefreshToken refreshToken, boolean requireReplacement) {
        if (refreshToken == null) {
            throw RefreshTokenException.invalidData("refreshToken", "Refresh token object is required");
        }
        if (!refreshToken.isRevoked()) {
            throw RefreshTokenException.invalidData("revoked", "Revoked must be true when revoking");
        }
        if (requireReplacement && refreshToken.getReplacedBy() == null) {
            throw RefreshTokenException.invalidData("replacedBy",
                    "ReplacedBy must reference the new token when revoking");
        }
    }
}
