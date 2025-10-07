package com.brokerx.auth_service.application.service;

import java.security.SecureRandom;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.beans.factory.annotation.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.otp.OtpCommand;
import com.brokerx.auth_service.application.port.in.useCase.OtpUseCase;
import com.brokerx.auth_service.application.port.out.OtpCachePort;
import com.brokerx.auth_service.application.port.out.UserRepositoryPort;
import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserStatus;
import com.brokerx.auth_service.domain.exception.otpCode.OtpException;
import com.brokerx.auth_service.domain.service.UserDomainValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService implements OtpUseCase {

    private static final Logger logger = LogManager.getLogger(OtpService.class);

    @Value("${otp.expires.in.seconds}")
    private int otpExpiresInSeconds;

    private final UserRepositoryPort userRepository;
    private final OtpCachePort otpCache;

    private final JwtService jwtService;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates and sends a 6-digit OTP code to the user's email for verification.
     */
    @Transactional
    public void sendOtp(User user) {
        if (otpCache.hasOtp(user.getEmail())) {
            logger.info("OTP already exists for user: {}", user.getEmail());
            return;
        }

        // Generate a 6-digit OTP code
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        otpCache.storeOtp(user.getEmail(), code, Duration.ofSeconds(otpExpiresInSeconds));

        logger.info("OTP generated and stored for user: {}", user.getEmail());

        // Send email after commit
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.sendOtp(user.getEmail(), code);
                logger.info("OTP email sent to: {}", user.getEmail());
            }
        });
    }

    /**
     * Verifies the provided OTP code and activates the user account if valid.
     */
    @Override
    @Transactional
    public LoginSuccess verifyOtp(OtpCommand request, String ipAddress, String userAgent) {
        logger.info("OTP verification attempt for email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("OTP verification failed - User not found: {}", request.getEmail());
                    return UserException.notFound(request.getEmail());
                });

        String storedOtp = otpCache.getOtp(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("OTP verification failed - OTP not found or expired for: {}", request.getEmail());
                    return OtpException.notFound(request.getEmail(), request.getCode());
                });

        if (!storedOtp.equals(request.getCode())) {
            logger.warn("OTP verification failed - Invalid code for: {}", request.getEmail());
            throw OtpException.notFound(request.getEmail(), request.getCode());
        }

        // Delete OTP after use
        otpCache.removeOtp(request.getEmail());

        user.setStatus(UserStatus.ACTIVE);
        UserDomainValidator.validateForUpdate(user);

        var updatedUser = userRepository.save(user);
        
        logger.info("OTP verified successfully, user activated: {}", updatedUser.getEmail());

        return buildAuthResponse(updatedUser, ipAddress, userAgent);
    }

    /**
     * Creates a successful login response with JWT access token for the authenticated user.
     */
    private LoginSuccess buildAuthResponse(User user, String ipAddress, String userAgent) {
        var accessToken = jwtService.generateToken(user);
        return LoginSuccess.builder()
                .accessToken(accessToken)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }
}