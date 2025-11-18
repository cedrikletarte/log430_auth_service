package com.brokerx.auth_service.application.port.in.command.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/* Command object representing a user registration request. */
@Getter
@Builder
@AllArgsConstructor
public class RegisterCommand {
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
