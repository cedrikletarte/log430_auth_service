package com.brokerx.auth_service.application.port.out;

public interface AuthenticatorPort {

    /**
     * Authenticate user with email and password
     */
    void authenticate(String email, String password);
}