package com.brokerx.auth_service.application.port.in.command.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginCommand {

    private String email;
    private String password;
}
