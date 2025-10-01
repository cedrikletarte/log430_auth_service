package com.brokerx.auth_service.application.port.out;

public interface AuthenticatorPort {
    void authenticate(String email, String password);
}