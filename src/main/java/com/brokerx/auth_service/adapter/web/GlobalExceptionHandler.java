package com.brokerx.auth_service.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.brokerx.auth_service.adapter.web.dto.ApiResponse;
import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.exception.otpCode.OtpException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /* Handles IllegalArgumentException by returning a JSON error response. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
            .badRequest()
            .body(new ApiResponse<>(
                "ERROR",
                "INVALID_ARGUMENT",
                ex.getMessage(),
                null
            ));
    }

    /* Handles UserException by returning a JSON error response. */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(UserException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(
                "ERROR",
                "USER_ERROR",
                ex.getMessage(),
                null
            ));
    }

    /* Handles RefreshTokenException by returning a JSON error response. */
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleRefreshTokenException(RefreshTokenException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>(
                "ERROR",
                "REFRESH_TOKEN_ERROR",
                ex.getMessage(),
                null
            ));
    }

    /* Handles OtpException by returning a JSON error response. */
    @ExceptionHandler(OtpException.class)
    public ResponseEntity<ApiResponse<Void>> handleOtpException(OtpException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(
                "ERROR",
                "OTP_ERROR",
                ex.getMessage(),
                null
            ));
    }

    /* Handles IllegalStateException by returning a JSON error response. */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiResponse<>(
                "ERROR",
                "ILLEGAL_STATE",
                ex.getMessage(),
                null
            ));
    }

    /* Catches any other unexpected exceptions. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>(
                "ERROR",
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                null
            ));
    }
}