package com.brokerx.auth_service.application.service;

import com.brokerx.auth_service.application.port.in.command.register.RegisterCommand;
import com.brokerx.auth_service.application.port.in.command.register.RegisterSuccess;
import com.brokerx.auth_service.application.port.in.useCase.RegisterUserUseCase;
import com.brokerx.auth_service.application.port.out.UserRepositoryPort;
import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserRole;
import com.brokerx.auth_service.domain.model.UserStatus;
import com.brokerx.auth_service.domain.service.UserDomainValidator;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final OtpService otpService;

    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new RegisterUserService with required dependencies for user registration.
     */
    public RegisterUserService(UserRepositoryPort userRepositoryPort, OtpService otpService,
            PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user by validating input data, creating user account, and sending OTP verification.
     */
    @Override
    @Transactional
    public RegisterSuccess register(RegisterCommand request) {
        String normalizedEmail = request.getEmail() == null ? null : request.getEmail().toLowerCase();
        if (userRepositoryPort.existsByEmail(normalizedEmail)) {
            throw UserException.alreadyExists(normalizedEmail);
        }

        String normalizedPostal = request.getPostalCode() == null ? null
                : request.getPostalCode().replaceAll("\\s+", "").toUpperCase().replace(" ", "");

        User toValidate = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(normalizedEmail)
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth()))
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(normalizedPostal)
                .role(UserRole.USER)
                .status(UserStatus.PENDING)
                .build();

        UserDomainValidator.validateForCreation(toValidate);

        toValidate.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = toValidate;

        var savedUser = userRepositoryPort.save(user);

        otpService.sendOtp(savedUser);

        return RegisterSuccess.builder()
                .email(savedUser.getEmail())
                .firstname(savedUser.getFirstname())
                .lastname(savedUser.getLastname())
                .otpPending(true)
                .message("Registration successful. Please check your email for OTP verification.")
                .build();
    }
}
