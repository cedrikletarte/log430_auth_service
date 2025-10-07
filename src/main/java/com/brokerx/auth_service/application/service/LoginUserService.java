package com.brokerx.auth_service.application.service;

import com.brokerx.auth_service.application.port.in.command.login.LoginCommand;
import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.useCase.LoginUserUseCase;
import com.brokerx.auth_service.application.port.out.UserRepositoryPort;
import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserStatus;
import com.brokerx.auth_service.application.port.out.AuthenticatorPort;

import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class LoginUserService implements LoginUserUseCase {

    private static final Logger logger = LogManager.getLogger(LoginUserService.class);

    private final UserRepositoryPort userRepository;

    private final JwtService jwtService;
    private final OtpService otpService;

    private final AuthenticatorPort authenticator;

    /**
     * Constructs a new LoginUserService with required dependencies for user authentication.
     */
    public LoginUserService(UserRepositoryPort userRepository, JwtService jwtService,
            OtpService otpService, AuthenticatorPort authenticator) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.authenticator = authenticator;
    }

    /**
     * Authenticates a user with email and password, handling OTP requirements and user status validation.
     */
    @Override
    public LoginSuccess login(LoginCommand loginCommand, String ipAddress, String userAgent) {
        logger.info("Login attempt for email: {}", loginCommand.getEmail());
        
        User user = userRepository.findByEmail(loginCommand.getEmail().toLowerCase())
                .orElseThrow(() -> {
                    logger.warn("Login failed - User not found: {}", loginCommand.getEmail());
                    return UserException.notFound(loginCommand.getEmail());
                });

        if (user.getStatus() == UserStatus.PENDING) {
            logger.info("User pending verification, sending OTP: {}", user.getEmail());
            otpService.sendOtp(user);

            return LoginSuccess.builder()
                    .otpPending(true)
                    .email(user.getEmail())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .accessToken(null)
                    .build();
        }
        if (user.getStatus().equals(UserStatus.SUSPENDED)) {
            logger.warn("Login failed - User suspended: {}", user.getEmail());
            throw UserException.notActive(user.getId(), user.getStatus().name());
        }

        authenticator.authenticate(loginCommand.getEmail(), loginCommand.getPassword());
        String accessToken = jwtService.generateToken(user);

        logger.info("Login successful for user: {}", user.getEmail());
        
        return LoginSuccess.builder()
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .accessToken(accessToken)
                .build();
    }

}