package com.brokerx.auth_service.domain.service;

import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.model.User;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@UtilityClass
public class UserDomainValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[1-9]([-. ]?[0-9]){9}$"
    );

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(
        "^[A-Za-z]\\d[A-Za-z]\\d[A-Za-z]\\d$"
    );



    private static final int MIN_AGE = 18;
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Validates all user fields required for creating a new user account.
     */
    public static void validateForCreation(User user) {
        validateBasicInfo(user);
        validateContactInfo(user);
        validateAddress(user);
        validateAge(user);
        validatePassword(user);
    }

    /**
     * Validates user data for updates including ID validation and optional password validation if provided.
     */
    public static void validateForUpdate(User user) {
        if (user.getId() == null) {
            throw UserException.invalid("id", "null", "ID is required for update");
        }
        validateBasicInfo(user);
        validateContactInfo(user);
        validateAddress(user);
        validateAge(user);
        // Password can be null for updates (means no change)
        if (user.getPassword() != null) {
            validatePassword(user);
        }
    }

    /**
     * Validates basic user information including first name and last name requirements and length constraints.
     */
    private static void validateBasicInfo(User user) {
    if (user.getFirstname() == null || user.getFirstname().trim().isEmpty()) {
        throw UserException.invalid("firstname", user.getFirstname(),
            "First name is required");
    }

    if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
        throw UserException.invalid("lastname", user.getLastname(),
            "Last name is required");
    }

    if (user.getFirstname().length() > 50) {
        throw UserException.invalid("firstname", user.getFirstname(),
            "First name must not exceed 50 characters");
    }

    if (user.getLastname().length() > 50) {
        throw UserException.invalid("lastname", user.getLastname(),
            "Last name must not exceed 50 characters");
    }
    }

    /**
     * Validates contact information including email format and optional phone number format validation.
     */
    private static void validateContactInfo(User user) {
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        throw UserException.invalid("email", user.getEmail(),
            "Email is required");
    }

    if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
        throw UserException.invalid("email", user.getEmail(),
            "Invalid email format");
    }

        if (user.getPhoneNumber() != null &&
                !user.getPhoneNumber().trim().isEmpty() &&
                !PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
        throw UserException.invalid("phoneNumber", user.getPhoneNumber(),
            "Invalid phone number format");
        }
    }

    /**
     * Validates address information including length constraints for address, city, and postal code format.
     */
    private static void validateAddress(User user) {
    if (user.getAddress() != null && user.getAddress().length() > 255) {
        throw UserException.invalid("address", user.getAddress(),
            "Address must not exceed 255 characters");
    }

    if (user.getCity() != null && user.getCity().length() > 100) {
        throw UserException.invalid("city", user.getCity(),
            "City must not exceed 100 characters");
    }

        if (user.getPostalCode() != null &&
                !user.getPostalCode().trim().isEmpty() &&
                !POSTAL_CODE_PATTERN.matcher(user.getPostalCode()).matches()) {
        throw UserException.invalid("postalCode", user.getPostalCode(),
            "Postal code must be 5 digits");
        }
    }

    /**
     * Validates that the user's date of birth is valid and meets the minimum age requirement.
     */
    private static void validateAge(User user) {
    if (user.getDateOfBirth() == null) {
        throw UserException.invalid("dateOfBirth", "null",
            "Date of birth is required");
    }

    if (user.getDateOfBirth().isAfter(LocalDate.now())) {
        throw UserException.invalid("dateOfBirth", user.getDateOfBirth().toString(),
            "Date of birth cannot be in the future");
    }

        int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
    if (age < MIN_AGE) {
        throw UserException.invalid("dateOfBirth", user.getDateOfBirth().toString(),
            "User must be at least " + MIN_AGE + " years old");
    }
    }

    /**
     * Validates password strength including length, uppercase, lowercase, and digit requirements.
     */
    private static void validatePassword(User user) {
    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
        throw UserException.invalid("password", "***",
            "Password is required");
    }

    if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
        throw UserException.invalid("password", "***",
            "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
    }

        // Ajoutez d'autres règles de mot de passe selon vos besoins
    if (!user.getPassword().matches(".*[A-Z].*")) {
        throw UserException.invalid("password", "***",
            "Password must contain at least one uppercase letter");
    }

    if (!user.getPassword().matches(".*[a-z].*")) {
        throw UserException.invalid("password", "***",
            "Password must contain at least one lowercase letter");
    }

        if (!user.getPassword().matches(".*[0-9].*")) {
            throw UserException.invalid("password", "***",
                    "Password must contain at least one digit");
        }
    }
}
