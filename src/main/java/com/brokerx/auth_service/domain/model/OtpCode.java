package com.brokerx.auth_service.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpCode {
    private Long id;
    private String code;
    private User user;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private boolean used;
}
