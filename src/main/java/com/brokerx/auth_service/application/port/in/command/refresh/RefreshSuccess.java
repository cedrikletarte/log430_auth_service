package com.brokerx.auth_service.application.port.in.command.refresh;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshSuccess {
    private String refreshToken;
    private Instant expiryDate;
}