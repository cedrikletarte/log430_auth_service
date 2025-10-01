package com.brokerx.auth_service.domain.exception.otpCode;

import java.time.LocalDateTime;

public class OtpException extends RuntimeException {
    private final String code; // OTP_INVALID_DATA, OTP_INVALID_FORMAT, OTP_NOT_FOUND, OTP_EXPIRED, OTP_ALREADY_USED, OTP_TOO_MANY_ATTEMPTS
    private final String field;
    private final String value;
    private final LocalDateTime expiresAt;
    private final Integer maxAttempts;

    private OtpException(String code, String field, String value, String message, LocalDateTime expiresAt, Integer maxAttempts) {
        super(message);
        this.code = code;
        this.field = field;
        this.value = value;
        this.expiresAt = expiresAt;
        this.maxAttempts = maxAttempts;
    }

    public static OtpException invalidData(String field, String value, String reason) {
        return new OtpException("OTP_INVALID_DATA", field, value,
                String.format("Invalid %s '%s': %s", field, value, reason), null, null);
    }

    public static OtpException invalidFormat(String reason) {
        return new OtpException("OTP_INVALID_FORMAT", "code", null,
                "Invalid OTP format: " + reason, null, null);
    }

    public static OtpException notFound(String email, String code) {
        return new OtpException("OTP_NOT_FOUND", "email", email,
                "OTP not found for email: " + email, null, null);
    }

    public static OtpException expired(LocalDateTime expiresAt) {
        return new OtpException("OTP_EXPIRED", "expiresAt", expiresAt.toString(),
                "OTP has expired at: " + expiresAt, expiresAt, null);
    }

    public static OtpException alreadyUsed() {
        return new OtpException("OTP_ALREADY_USED", "used", "true",
                "OTP has already been used", null, null);
    }

    public static OtpException tooManyAttempts(int maxAttempts) {
        return new OtpException("OTP_TOO_MANY_ATTEMPTS", "attempts", String.valueOf(maxAttempts),
                "Too many OTP verification attempts", null, maxAttempts);
    }

    public String getCode() { return code; }
    public String getField() { return field; }
    public String getValue() { return value; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public Integer getMaxAttempts() { return maxAttempts; }
}
