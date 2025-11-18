package com.brokerx.auth_service.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.brokerx.auth_service.domain.model.RefreshToken;
import com.brokerx.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;

@Component
public class RefreshTokenMapper {

    private final UserMapper userMapper;

    /* Constructs a RefreshTokenMapper with user mapper dependency for handling user relationships. */
    public RefreshTokenMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /* Converts a RefreshToken domain object to a RefreshTokenEntity for database persistence with shallow mapping of replacedBy to avoid recursion. */
    public RefreshTokenEntity toEntity(RefreshToken token) {
        if (token == null)
            return null;

        // Map replacedBy shallowly to avoid deep recursion and transient graphs
        RefreshTokenEntity replacedByEntity = null;
        if (token.getReplacedBy() != null && token.getReplacedBy().getId() != null) {
            replacedByEntity = new RefreshTokenEntity();
            replacedByEntity.setId(token.getReplacedBy().getId());
        }

        return RefreshTokenEntity.builder()
                .id(token.getId())
                .user(userMapper.toEntity(token.getUser()))
                .token(token.getToken())
                .expiryDate(token.getExpiryDate())
                .revoked(token.isRevoked())
                .ipAddress(token.getIpAddress())
                .userAgent(token.getUserAgent())
                .createdAt(token.getCreatedAt())
                .replacedBy(replacedByEntity)
                .build();
    }

    /* Converts a RefreshTokenEntity from database to a RefreshToken domain object with shallow mapping of replacedBy to avoid infinite recursion. */
    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null)
            return null;

        // Map replacedBy shallowly (id only) to avoid infinite recursion on cycles
        RefreshToken replacedBy = null;
        if (entity.getReplacedBy() != null) {
            replacedBy = RefreshToken.builder()
                    .id(entity.getReplacedBy().getId())
                    .build();
        }

        return RefreshToken.builder()
                .id(entity.getId())
                .user(userMapper.toDomain(entity.getUser()))
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .isRevoked(entity.isRevoked())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .replacedBy(replacedBy)
                .build();
    }
}