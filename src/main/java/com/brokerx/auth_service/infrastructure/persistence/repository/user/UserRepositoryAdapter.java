package com.brokerx.auth_service.infrastructure.persistence.repository.user;

import com.brokerx.auth_service.application.port.out.UserRepositoryPort;
import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.infrastructure.persistence.entity.UserEntity;
import com.brokerx.auth_service.infrastructure.persistence.mapper.UserMapper;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringUserRepository springUserRepository;
    private final UserMapper userMapper;

    /* Constructs a UserRepositoryAdapter with Spring repository and domain mapper dependencies. */
    public UserRepositoryAdapter(SpringUserRepository springUserRepository, UserMapper userMapper) {
        this.springUserRepository = springUserRepository;
        this.userMapper = userMapper;
    }

    /* Persists a user domain object by converting to entity, saving, and converting back to domain. */
    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        entity = springUserRepository.save(entity);
        return userMapper.toDomain(entity);
    }

    /* Persists a list of user domain objects in batch by converting to entities, saving, and converting back to domains. */
    @Override
    public List<User> saveAll(List<User> users) {
    List<UserEntity> entities = users.stream()
        .map(userMapper::toEntity)
        .toList();
    List<UserEntity> savedEntities = springUserRepository.saveAll(entities);
    return savedEntities.stream()
        .map(userMapper::toDomain)
        .toList();
    }

    @Override
    public Optional<User> findById(Long id) {
    return springUserRepository.findById(id)
        .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
    return springUserRepository.findAll().stream()
        .map(userMapper::toDomain)
        .toList();
    }

    /* Finds a user by email address and converts the entity to domain object if found. */
    @Override
    public Optional<User> findByEmail(String email) {
        return springUserRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    /* Checks if a user with the specified email address already exists in the database. */
    @Override
    public boolean existsByEmail(String email) {
        return springUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return springUserRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        springUserRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        springUserRepository.deleteAll();
    }
}