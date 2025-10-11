package com.brokerx.auth_service.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.model.User;

class UserDomainValidatorTest {

    private User createValidUser() {
        return User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .phoneNumber("5141234567")
                .address("123 Main St")
                .city("Montreal")
                .postalCode("H1A2B3")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .password("Password123")
                .build();
    }

    @Test
    void shouldValidateUserForCreation() {
        User user = createValidUser();
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldRejectNullFirstname() {
        User user = createValidUser();
        user.setFirstname(null);

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("First name is required"));
    }

    @Test
    void shouldRejectEmptyFirstname() {
        User user = createValidUser();
        user.setFirstname("   ");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("First name is required"));
    }

    @Test
    void shouldRejectTooLongFirstname() {
        User user = createValidUser();
        user.setFirstname("a".repeat(51));

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("First name must not exceed 50 characters"));
    }

    @Test
    void shouldRejectNullLastname() {
        User user = createValidUser();
        user.setLastname(null);

        assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        User user = createValidUser();
        user.setEmail("invalid-email");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    void shouldRejectEmailWithoutDomain() {
        User user = createValidUser();
        user.setEmail("test@");

        assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldAcceptValidEmails() {
        User user = createValidUser();
        
        user.setEmail("test@example.com");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
        
        user.setEmail("user.name@example.co.uk");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
        
        user.setEmail("test+tag@domain.com");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldRejectInvalidPhoneNumber() {
        User user = createValidUser();
        user.setPhoneNumber("123");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Invalid phone number format"));
    }

    @Test
    void shouldRejectPhoneNumberStartingWithZero() {
        User user = createValidUser();
        user.setPhoneNumber("0141234567");

        assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldAcceptValidPhoneNumbers() {
        User user = createValidUser();
        
        user.setPhoneNumber("5141234567");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
        
        user.setPhoneNumber("514-123-4567");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldRejectInvalidPostalCode() {
        User user = createValidUser();
        user.setPostalCode("12345");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Postal code must be 5 digits"));
    }

    @Test
    void shouldAcceptValidPostalCodes() {
        User user = createValidUser();
        
        user.setPostalCode("H1A2B3");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
        
        user.setPostalCode("h1a2b3");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldRejectNullDateOfBirth() {
        User user = createValidUser();
        user.setDateOfBirth(null);

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Date of birth is required"));
    }

    @Test
    void shouldRejectFutureDateOfBirth() {
        User user = createValidUser();
        user.setDateOfBirth(LocalDate.now().plusDays(1));

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Date of birth cannot be in the future"));
    }

    @Test
    void shouldRejectUnderageUser() {
        User user = createValidUser();
        user.setDateOfBirth(LocalDate.now().minusYears(17));

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("User must be at least 18 years old"));
    }

    @Test
    void shouldAcceptUserExactly18YearsOld() {
        User user = createValidUser();
        user.setDateOfBirth(LocalDate.now().minusYears(18));

        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldRejectNullPassword() {
        User user = createValidUser();
        user.setPassword(null);

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Password is required"));
    }

    @Test
    void shouldRejectShortPassword() {
        User user = createValidUser();
        user.setPassword("Pass1");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Password must be at least 8 characters long"));
    }

    @Test
    void shouldRejectPasswordWithoutUppercase() {
        User user = createValidUser();
        user.setPassword("password123");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Password must contain at least one uppercase letter"));
    }

    @Test
    void shouldRejectPasswordWithoutLowercase() {
        User user = createValidUser();
        user.setPassword("PASSWORD123");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Password must contain at least one lowercase letter"));
    }

    @Test
    void shouldRejectPasswordWithoutDigit() {
        User user = createValidUser();
        user.setPassword("PasswordABC");

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForCreation(user));
        assertTrue(exception.getMessage().contains("Password must contain at least one digit"));
    }

    @Test
    void shouldAcceptStrongPassword() {
        User user = createValidUser();
        
        user.setPassword("Password123");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
        
        user.setPassword("MyP@ssw0rd!");
        assertDoesNotThrow(() -> UserDomainValidator.validateForCreation(user));
    }

    @Test
    void shouldValidateUserForUpdateWithId() {
        User user = createValidUser();
        user.setId(1L);

        assertDoesNotThrow(() -> UserDomainValidator.validateForUpdate(user));
    }

    @Test
    void shouldRejectUpdateWithoutId() {
        User user = createValidUser();
        user.setId(null);

        UserException exception = assertThrows(UserException.class,
                () -> UserDomainValidator.validateForUpdate(user));
        assertTrue(exception.getMessage().contains("ID is required for update"));
    }

    @Test
    void shouldAllowNullPasswordForUpdate() {
        User user = createValidUser();
        user.setId(1L);
        user.setPassword(null);

        assertDoesNotThrow(() -> UserDomainValidator.validateForUpdate(user));
    }
}
