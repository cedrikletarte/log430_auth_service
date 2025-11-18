package com.brokerx.auth_service.adapter.web.dto;

import lombok.Data;

/* DTO for user registration requests. */
@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
}
