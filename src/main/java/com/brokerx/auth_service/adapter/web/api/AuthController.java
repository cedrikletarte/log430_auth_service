package com.brokerx.auth_service.adapter.web.api;

import com.brokerx.auth_service.adapter.web.dto.LoginRequest;
import com.brokerx.auth_service.adapter.web.dto.RegisterRequest;
import com.brokerx.auth_service.adapter.web.dto.VerifyOtpRequest;
import com.brokerx.auth_service.adapter.web.dto.ApiResponse;
import com.brokerx.auth_service.application.port.in.command.login.LoginCommand;
import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.otp.OtpCommand;
import com.brokerx.auth_service.application.port.in.command.refresh.RefreshSuccess;
import com.brokerx.auth_service.application.port.in.command.register.RegisterCommand;
import com.brokerx.auth_service.application.port.in.command.register.RegisterSuccess;
import com.brokerx.auth_service.application.port.in.useCase.LoginUserUseCase;
import com.brokerx.auth_service.application.port.in.useCase.LogoutUserUseCase;
import com.brokerx.auth_service.application.port.in.useCase.OtpUseCase;
import com.brokerx.auth_service.application.port.in.useCase.RefreshTokenUserUseCase;
import com.brokerx.auth_service.application.port.in.useCase.RegisterUserUseCase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private static final Logger log = LoggerFactory.getLogger(AuthController.class);

        private final RegisterUserUseCase registerUserUseCase;
        private final LoginUserUseCase loginUserUseCase;
        private final LogoutUserUseCase logoutUserUseCase;
        private final RefreshTokenUserUseCase refreshTokenUserUseCase;
        private final OtpUseCase otpUseCase;
        
        /**
         * Constructs a new AuthController with the required use cases for authentication operations.
         */
        public AuthController(RegisterUserUseCase registerUserUseCase,
                        LoginUserUseCase loginUserUseCase,
                        LogoutUserUseCase logoutUserUseCase,
                        RefreshTokenUserUseCase refreshTokenUserUseCase,
                        OtpUseCase otpUseCase) {
                this.registerUserUseCase = registerUserUseCase;
                this.loginUserUseCase = loginUserUseCase;
                this.logoutUserUseCase = logoutUserUseCase;
                this.refreshTokenUserUseCase = refreshTokenUserUseCase;
                this.otpUseCase = otpUseCase;
        }

        /**
         * Authenticates a user with email and password, optionally requiring OTP verification.
         */
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginSuccess>> login(@RequestBody LoginRequest body,
                        HttpServletRequest request,
                        HttpServletResponse response) {

                String ipAddress = request.getHeader("X-Client-Real-IP");
                String userAgent = request.getHeader("X-Client-User-Agent");

                LoginCommand loginCommand = LoginCommand.builder()
                                .email(body.getEmail())
                                .password(body.getPassword())
                                .build();

                LoginSuccess loginSuccess = loginUserUseCase.login(loginCommand, ipAddress, userAgent);

                if (loginSuccess.isOtpPending()) {
                        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(
                                        "PENDING",
                                        "OTP_REQUIRED",
                                        "An OTP has been sent to your email",
                                        loginSuccess));
                }

                return ResponseEntity.status(HttpStatus.OK)
                                .header("Set-Cookie",
                                                buildRefreshCookie(refreshTokenUserUseCase.createRefreshToken(
                                                                loginSuccess.getEmail(), ipAddress, userAgent))
                                                                .toString())
                                .body(new ApiResponse<>(
                                                "SUCCESS",
                                                null,
                                                "Login successful",
                                                loginSuccess));
        }

        /**
         * Verifies the OTP code sent to user's email during the authentication process.
         */
        @PostMapping("/verify-otp")
        public ResponseEntity<ApiResponse<LoginSuccess>> verifyOtp(@RequestBody VerifyOtpRequest body,
                        HttpServletRequest request,
                        HttpServletResponse response) {

                String ipAddress = request.getHeader("X-Client-Real-IP");
                String userAgent = request.getHeader("X-Client-User-Agent");

                OtpCommand otpCommand = OtpCommand.builder()
                                .email(body.getEmail())
                                .code(body.getCode())
                                .build();

                LoginSuccess loginSuccess = otpUseCase.verifyOtp(otpCommand, ipAddress, userAgent);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .header("Set-Cookie",
                                                buildRefreshCookie(refreshTokenUserUseCase.createRefreshToken(
                                                                loginSuccess.getEmail(), ipAddress, userAgent))
                                                                .toString())
                                .body(new ApiResponse<>(
                                                "SUCCESS",
                                                null,
                                                "OTP verified and login successful",
                                                loginSuccess));
        }

        /**
         * Registers a new user account and sends an OTP for email verification.
         */
        @PostMapping("/register")
        public ResponseEntity<ApiResponse<RegisterSuccess>> register(@RequestBody RegisterRequest body) {

                // Build the RegisterCommand from the request body
                RegisterCommand registerCommand = RegisterCommand.builder()
                                .email(body.getEmail())
                                .password(body.getPassword())
                                .firstName(body.getFirstName())
                                .lastName(body.getLastName())
                                .phoneNumber(body.getPhoneNumber())
                                .dateOfBirth(body.getDateOfBirth())
                                .address(body.getAddress())
                                .city(body.getCity())
                                .postalCode(body.getPostalCode())
                                .build();

                // Call the use case to register the user
                RegisterSuccess registerSuccess = registerUserUseCase.register(registerCommand);

                // Check if OTP is pending
                if (registerSuccess.isOtpPending()) {

                        // Return the Location header to the OTP verification endpoint
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(new ApiResponse<>(
                                                        "PENDING",
                                                        "OTP_REQUIRED",
                                                        "An OTP has been sent to your email",
                                                        registerSuccess));
                }

                // Return a conflict response with the response body
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(new ApiResponse<>(
                                                "EXISTS",
                                                "USER_EXISTS",
                                                "User already exists",
                                                null));
        }

        /**
         * Logs out a user by revoking their refresh token and clearing authentication cookies.
         */
        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Map<String, String>>> logout(
                        @CookieValue(required = true) String refreshToken,
                        HttpServletRequest request,
                        HttpServletResponse response) {

                try {
                        logoutUserUseCase.logout(refreshToken);
                } catch (Exception e) {
                        log.error("Logout failed", e);
                }

                ResponseCookie clearRefreshToken = ResponseCookie.from("refreshToken", "")
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .sameSite("Lax")
                        .maxAge(0)
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .header("Set-Cookie", clearRefreshToken.toString())
                        .body(new ApiResponse<>("SUCCESS", null, 
                        "Logout successful", null));
        }

        /**
         * Refreshes an expired access token using a valid refresh token.
         */
        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<LoginSuccess>> refresh(
                        @CookieValue(required = true) String refreshToken,
                        HttpServletRequest request) {
                if (refreshToken == null || refreshToken.isEmpty()) {
                        ApiResponse<LoginSuccess> resp = new ApiResponse<>(
                                        "ERROR",
                                        "NO_REFRESH_TOKEN",
                                        "No refresh token provided",
                                        null);

                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(resp);
                }

                String ipAddress = request.getHeader("X-Client-Real-IP");
                String userAgent = request.getHeader("X-Client-User-Agent");

                LoginSuccess loginSuccess = refreshTokenUserUseCase.refreshToken(refreshToken);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .header("Set-Cookie", buildRefreshCookie(refreshTokenUserUseCase.createRefreshToken(
                                                loginSuccess.getEmail(), ipAddress, userAgent)).toString())
                                .body(new ApiResponse<>(
                                                "SUCCESS",
                                                null,
                                                "Token refreshed successfully",
                                                loginSuccess));
        }

        /**
         * Builds a secure HTTP-only cookie containing the refresh token with appropriate security settings.
         */
        private ResponseCookie buildRefreshCookie(RefreshSuccess refreshToken) {
                Instant expiry = refreshToken.getExpiryDate();
                long maxAgeSeconds = Duration.between(Instant.now(), expiry).getSeconds();
                return ResponseCookie.from("refreshToken", refreshToken.getRefreshToken())
                                .httpOnly(true)
                                .secure(false) // Set to true in production with HTTPS
                                .path("/")
                                .sameSite("Lax")
                                .maxAge(maxAgeSeconds)
                                .build();
        }
}
