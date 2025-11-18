package com.brokerx.auth_service.application.port.in.useCase;

import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.otp.OtpCommand;
import com.brokerx.auth_service.domain.model.User;

public interface OtpUseCase {

    /* Sends an OTP to the specified user. */
    void sendOtp(User user);

    /* Verifies the provided OTP command and returns a login success response. */
    LoginSuccess verifyOtp(OtpCommand otpCommand, String ipAddress, String userAgent);
}
