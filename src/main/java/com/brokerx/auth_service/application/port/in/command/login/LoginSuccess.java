package com.brokerx.auth_service.application.port.in.command.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccess {

    @Builder.Default
    private boolean otpPending = false;

    private String accessToken;
    private String firstname;
    private String lastname;
    private String email;
}
