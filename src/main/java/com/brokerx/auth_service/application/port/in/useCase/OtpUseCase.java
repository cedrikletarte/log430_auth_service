package com.brokerx.auth_service.application.port.in.useCase;

import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;
import com.brokerx.auth_service.application.port.in.command.otp.OtpCommand;
import com.brokerx.auth_service.domain.model.User;

public interface OtpUseCase {
    void sendOtp(User user);

    LoginSuccess verifyOtp(OtpCommand otpCommand, String ipAddress, String userAgent);
}
