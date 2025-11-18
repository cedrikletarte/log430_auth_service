package com.brokerx.auth_service.application.port.out;

public interface PasswordEncoderPort {

    /* Match raw password with encoded password */
    boolean matches(String rawPassword, String encodedPassword);
}

