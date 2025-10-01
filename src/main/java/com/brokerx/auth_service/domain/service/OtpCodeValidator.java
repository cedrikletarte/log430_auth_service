package com.brokerx.auth_service.domain.service;

import com.brokerx.auth_service.domain.exception.otpCode.OtpException;
import com.brokerx.auth_service.domain.model.OtpCode;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class OtpCodeValidator {

    /**
     * Validates that the OTP code is not null, not empty, and consists of exactly 6 digits.
     */
    public static void validateCodeFormat(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw OtpException.invalidFormat("code is required");
        }
        if (!code.matches("^[0-9]{6}$")) {
            throw OtpException.invalidFormat("code must be exactly 6 digits");
        }
    }

    /**
     * Validates all required fields for creating a new OTP code including user, dates, and unused status.
     */
    public static void validateForCreation(OtpCode otp) {
        if (otp == null) {
            throw OtpException.invalidData("otp", "null", "OtpCode object is required");
        }
        if (otp.getUser() == null || otp.getUser().getEmail() == null) {
            throw OtpException.invalidData("user.email", "null", "User email is required");
        }
        validateCodeFormat(otp.getCode());

        if (otp.getCreatedAt() == null) {
            throw OtpException.invalidData("createdAt", "null", "Creation date is required");
        }
        if (otp.getExpiresAt() == null) {
            throw OtpException.invalidData("expiresAt", "null", "Expiry date is required");
        }
        if (!otp.getExpiresAt().isAfter(otp.getCreatedAt())) {
            throw OtpException.invalidData("expiresAt", otp.getExpiresAt().toString(),
                    "Expiry must be after creation");
        }
        if (otp.isUsed()) {
            throw OtpException.invalidData("used", "true", "OTP must be unused on creation");
        }
    }

    /**
     * Validates that an OTP code is still valid for verification by checking expiration and usage status.
     */
    public static void validateForVerification(OtpCode otp) {
        if (otp == null) {
            throw OtpException.invalidData("otp", "null", "OtpCode object is required");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw OtpException.expired(otp.getExpiresAt());
        }
        if (otp.isUsed()) {
            throw OtpException.alreadyUsed();
        }
    }
}
