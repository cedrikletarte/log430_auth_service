package com.brokerx.auth_service.application.port.out;

public interface PasswordEncoderPort {
    boolean matches(String rawPassword, String encodedPassword);
}

