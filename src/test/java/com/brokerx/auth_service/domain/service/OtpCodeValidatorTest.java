package com.brokerx.auth_service.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.brokerx.auth_service.domain.exception.otpCode.OtpException;
import com.brokerx.auth_service.domain.model.OtpCode;
import com.brokerx.auth_service.domain.model.User;

class OtpCodeValidatorTest {

    @Test
    void shouldValidateCorrectOtpCodeFormat() {
        assertDoesNotThrow(() -> OtpCodeValidator.validateCodeFormat("123456"));
        assertDoesNotThrow(() -> OtpCodeValidator.validateCodeFormat("000000"));
        assertDoesNotThrow(() -> OtpCodeValidator.validateCodeFormat("999999"));
    }

    @Test
    void shouldRejectNullOtpCode() {
        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateCodeFormat(null));
        assertEquals("Invalid OTP format: code is required", exception.getMessage());
    }

    @Test
    void shouldRejectEmptyOtpCode() {
        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateCodeFormat(""));
        assertEquals("Invalid OTP format: code is required", exception.getMessage());
    }

    @Test
    void shouldRejectOtpCodeWithLessThan6Digits() {
        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateCodeFormat("12345"));
        assertEquals("Invalid OTP format: code must be exactly 6 digits", exception.getMessage());
    }

    @Test
    void shouldRejectOtpCodeWithMoreThan6Digits() {
        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateCodeFormat("1234567"));
        assertEquals("Invalid OTP format: code must be exactly 6 digits", exception.getMessage());
    }

    @Test
    void shouldRejectOtpCodeWithLetters() {
        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateCodeFormat("12A456"));
        assertEquals("Invalid OTP format: code must be exactly 6 digits", exception.getMessage());
    }

    @Test
    void shouldValidateOtpForCreation() {
        User user = User.builder()
                .email("test@example.com")
                .build();

        OtpCode otp = OtpCode.builder()
                .code("123456")
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        assertDoesNotThrow(() -> OtpCodeValidator.validateForCreation(otp));
    }

    @Test
    void shouldRejectNullOtpForCreation() {
        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateForCreation(null));
        assertEquals("Invalid otp 'null': OtpCode object is required", exception.getMessage());
    }

    @Test
    void shouldRejectOtpWithoutUser() {
        OtpCode otp = OtpCode.builder()
                .code("123456")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateForCreation(otp));
        assertEquals("Invalid user.email 'null': User email is required", exception.getMessage());
    }

    @Test
    void shouldRejectOtpWithExpiryBeforeCreation() {
        User user = User.builder().email("test@example.com").build();
        LocalDateTime now = LocalDateTime.now();

        OtpCode otp = OtpCode.builder()
                .code("123456")
                .user(user)
                .createdAt(now)
                .expiresAt(now.minusMinutes(1))
                .used(false)
                .build();

        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateForCreation(otp));
        assertEquals("Invalid expiresAt '" + now.minusMinutes(1) + "': Expiry must be after creation", exception.getMessage());
    }

    @Test
    void shouldRejectAlreadyUsedOtpForCreation() {
        User user = User.builder().email("test@example.com").build();

        OtpCode otp = OtpCode.builder()
                .code("123456")
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(true)
                .build();

        OtpException exception = assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateForCreation(otp));
        assertEquals("Invalid used 'true': OTP must be unused on creation", exception.getMessage());
    }

    @Test
    void shouldValidateOtpForVerification() {
        OtpCode otp = OtpCode.builder()
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        assertDoesNotThrow(() -> OtpCodeValidator.validateForVerification(otp));
    }

    @Test
    void shouldRejectExpiredOtpForVerification() {
        OtpCode otp = OtpCode.builder()
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateForVerification(otp));
    }

    @Test
    void shouldRejectUsedOtpForVerification() {
        OtpCode otp = OtpCode.builder()
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(true)
                .build();

        assertThrows(OtpException.class,
                () -> OtpCodeValidator.validateForVerification(otp));
    }
}
