package com.brokerx.auth_service.application.port.in.useCase;

import com.brokerx.auth_service.application.port.in.command.register.RegisterCommand;
import com.brokerx.auth_service.application.port.in.command.register.RegisterSuccess;

public interface RegisterUserUseCase {
    RegisterSuccess register(RegisterCommand registerCommand);
}