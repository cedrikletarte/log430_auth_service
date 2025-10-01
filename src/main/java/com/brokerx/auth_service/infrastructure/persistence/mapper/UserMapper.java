package com.brokerx.auth_service.infrastructure.persistence.mapper;

import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Converts a User domain object to a UserEntity for database persistence.
     */
    public UserEntity toEntity(User user) {
        if (user == null)
            return null;
        return UserEntity.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .city(user.getCity())
                .postalCode(user.getPostalCode())
                .status(user.getStatus())
                .role(user.getRole())
                .build();
    }

    /**
     * Converts a UserEntity from database to a User domain object.
     */
    public User toDomain(UserEntity entity) {
        if (entity == null)
            return null;
        return User.builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .phoneNumber(entity.getPhoneNumber())
                .dateOfBirth(entity.getDateOfBirth())
                .address(entity.getAddress())
                .city(entity.getCity())
                .postalCode(entity.getPostalCode())
                .status(entity.getStatus())
                .role(entity.getRole())
                .build();
    }
}
