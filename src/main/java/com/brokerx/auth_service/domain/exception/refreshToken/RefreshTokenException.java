package com.brokerx.auth_service.domain.exception.refreshToken;

import java.time.LocalDateTime;

public class RefreshTokenException extends RuntimeException {
    private final String code; // REFRESH_INVALID_DATA, REFRESH_INVALID_FORMAT, REFRESH_NOT_FOUND, REFRESH_EXPIRED, REFRESH_REVOKED
    private final String field;
    private final String value;
    private final LocalDateTime expiryDate;

    private RefreshTokenException(String code, String field, String value, String message, LocalDateTime expiryDate) {
        super(message);
        this.code = code;
        this.field = field;
        this.value = value;
        this.expiryDate = expiryDate;
    }

    public static RefreshTokenException invalidData(String field, String reason) {
        return new RefreshTokenException("REFRESH_INVALID_DATA", field, null,
                String.format("Invalid %s: %s", field, reason), null);
    }

    public static RefreshTokenException invalidFormat(String reason) {
        return new RefreshTokenException("REFRESH_INVALID_FORMAT", "token", null,
                "Invalid refresh token format: " + reason, null);
    }

    public static RefreshTokenException notFound(String token) {
        return new RefreshTokenException("REFRESH_NOT_FOUND", "token", token,
                "Refresh token not found: " + token, null);
    }

    public static RefreshTokenException expired(String token, LocalDateTime expiry) {
        return new RefreshTokenException("REFRESH_EXPIRED", "token", token,
                "Refresh token expired at: " + expiry, expiry);
    }

    public static RefreshTokenException revoked(String token) {
        return new RefreshTokenException("REFRESH_REVOKED", "token", token,
                "Refresh token has been revoked", null);
    }

    public String getCode() { return code; }
    public String getField() { return field; }
    public String getValue() { return value; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
}
