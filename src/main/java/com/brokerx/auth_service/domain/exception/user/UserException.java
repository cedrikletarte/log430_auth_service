package com.brokerx.auth_service.domain.exception.user;

public class UserException extends RuntimeException {
    private final String code; // e.g. USER_INVALID_DATA, USER_ALREADY_EXISTS, USER_NOT_FOUND, USER_NOT_ACTIVE, USER_UNAUTHORIZED
    private final String field;
    private final String value;
    private final String extra;

    private UserException(String code, String field, String value, String message, String extra) {
        super(message);
        this.code = code;
        this.field = field;
        this.value = value;
        this.extra = extra;
    }

    public static UserException invalid(String field, String value, String reason) {
        return new UserException("USER_INVALID_DATA", field, value,
                String.format("Invalid %s '%s': %s", field, value, reason), null);
    }

    public static UserException alreadyExists(String email) {
        return new UserException("USER_ALREADY_EXISTS", "email", email,
                "User already exists with email: " + email, null);
    }

    public static UserException notFound(String identifier) {
        return new UserException("USER_NOT_FOUND", "identifier", identifier,
                "User not found with identifier: " + identifier, null);
    }

    public static UserException notActive(Long id, String status) {
        return new UserException("USER_NOT_ACTIVE", "id", id != null ? id.toString() : null,
                String.format("User %s is not active. Current status: %s", id, status), status);
    }

    public static UserException unauthorized(Long id, String role, String operation) {
        return new UserException("USER_UNAUTHORIZED", "id", id != null ? id.toString() : null,
                String.format("User %s with role %s is not authorized to perform: %s", id, role, operation), operation);
    }

    public String getCode() { return code; }
    public String getField() { return field; }
    public String getValue() { return value; }
    public String getExtra() { return extra; }
}
