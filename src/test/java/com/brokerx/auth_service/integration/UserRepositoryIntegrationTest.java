package com.brokerx.auth_service.integration;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserRole;
import com.brokerx.auth_service.domain.model.UserStatus;
import com.brokerx.auth_service.infrastructure.persistence.mapper.UserMapper;
import com.brokerx.auth_service.infrastructure.persistence.repository.user.UserRepositoryAdapter;

@Testcontainers
@DataJpaTest
@Import({UserRepositoryAdapter.class, UserMapper.class})
class UserRepositoryIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private UserRepositoryAdapter userRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setAddress("1234 rue LaPlace");
        user.setPhoneNumber("1234567892");
        user.setCity("Montreal");
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));
        user.setPostalCode("H1A2B3");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmail("john@example.com");
        user.setPassword("password");
        userRepository.save(user);

        User found = userRepository.findByEmail("john@example.com").orElseThrow();
        assertEquals("John", found.getFirstname());
    }
}
