package com.brokerx.auth_service.adapter.web.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String code;
}
