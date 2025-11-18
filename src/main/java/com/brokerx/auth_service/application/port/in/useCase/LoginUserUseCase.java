package com.brokerx.auth_service.application.port.in.useCase;

import com.brokerx.auth_service.application.port.in.command.login.LoginCommand;
import com.brokerx.auth_service.application.port.in.command.login.LoginSuccess;

public interface LoginUserUseCase {

    /* Logs in a user with the provided login command, IP address, and user agent. */
    LoginSuccess login(LoginCommand loginCommand, String ipAddress, String userAgent);
}
