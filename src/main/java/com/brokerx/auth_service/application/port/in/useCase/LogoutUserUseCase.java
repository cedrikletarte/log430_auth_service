package com.brokerx.auth_service.application.port.in.useCase;

public interface LogoutUserUseCase {

    /* Logs out a user by invalidating the provided refresh token. */
    void logout(String refreshToken);
}