package com.brokerx.auth_service.application.port.in.command.refresh;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshSuccess {
    private String refreshToken;
    private LocalDateTime expiryDate;
}