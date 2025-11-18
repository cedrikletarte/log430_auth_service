package com.brokerx.auth_service.application.port.in.command.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/* Command object representing a login request with email and password. */
@Getter
@Builder
@AllArgsConstructor
public class LoginCommand {

    private String email;
    private String password;
}
