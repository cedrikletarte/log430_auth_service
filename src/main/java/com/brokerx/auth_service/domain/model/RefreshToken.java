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
public class RefreshToken {
    private Long id;
    private String token;
    private User user;
    private LocalDateTime expiryDate;
    private RefreshToken replacedBy;
    private LocalDateTime createdAt;
    private String ipAddress;
    private String userAgent;

    @Builder.Default
    private boolean isRevoked = false;
}
