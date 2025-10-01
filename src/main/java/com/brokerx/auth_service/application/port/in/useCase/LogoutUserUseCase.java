package com.brokerx.auth_service.application.port.in.useCase;

public interface LogoutUserUseCase {
    void logout(String refreshToken);
}