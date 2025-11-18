package com.brokerx.auth_service.infrastructure.persistence.repository.refreshToken;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.brokerx.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.brokerx.auth_service.domain.model.RefreshToken;
import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;
import com.brokerx.auth_service.infrastructure.persistence.entity.UserEntity;
import com.brokerx.auth_service.infrastructure.persistence.mapper.RefreshTokenMapper;
import com.brokerx.auth_service.infrastructure.persistence.mapper.UserMapper;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final SpringRefreshTokenRepository springRefreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;
    private final UserMapper userMapper;

    /* Constructs a RefreshTokenRepositoryAdapter with Spring repository and mapper dependencies. */
    public RefreshTokenRepositoryAdapter(SpringRefreshTokenRepository springRefreshTokenRepository,
            RefreshTokenMapper refreshTokenMapper,
            UserMapper userMapper) {
        this.springRefreshTokenRepository = springRefreshTokenRepository;
        this.refreshTokenMapper = refreshTokenMapper;
        this.userMapper = userMapper;
    }

    /* Persists a refresh token domain object by converting to entity, saving, and converting back to domain. */
    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = refreshTokenMapper.toEntity(token);
        entity = springRefreshTokenRepository.save(entity);
        return refreshTokenMapper.toDomain(entity);
    }

    /* Finds a refresh token by its token string value and converts the entity to domain object if found. */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return springRefreshTokenRepository.findByToken(token)
                .map(refreshTokenMapper::toDomain);
    }

    /* Finds a refresh token for a specific user, IP address, user agent, and revocation status. */
    @Override
    public Optional<RefreshToken> findByUserAndIpAddressAndUserAgentAndRevoked(User user, String ip, String userAgent,
            boolean revoked) {
        UserEntity userEntity = userMapper.toEntity(user);
        return springRefreshTokenRepository
                .findByUserAndIpAddressAndUserAgentAndRevoked(userEntity, ip, userAgent, revoked)
                .map(refreshTokenMapper::toDomain);
    }
}
