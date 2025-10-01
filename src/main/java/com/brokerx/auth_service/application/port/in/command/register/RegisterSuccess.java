package com.brokerx.auth_service.application.port.in.command.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterSuccess {

    @Builder.Default
    private boolean otpPending = false;

    private String firstname;
    private String lastname;
    private String email;
    private String message;
}