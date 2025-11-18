package com.brokerx.auth_service.application.port.in.command.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/* Command object representing an OTP verification request. */
@Getter
@Builder
@AllArgsConstructor
public class OtpCommand {

    private String email;
    private String code;
}
