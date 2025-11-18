package com.brokerx.auth_service.application.port.in.useCase;

import com.brokerx.auth_service.application.port.in.command.register.RegisterCommand;
import com.brokerx.auth_service.application.port.in.command.register.RegisterSuccess;

public interface RegisterUserUseCase {
    
    /* Registers a new user with the provided registration command. */
    RegisterSuccess register(RegisterCommand registerCommand);
}