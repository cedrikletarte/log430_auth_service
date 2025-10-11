package com.brokerx.auth_service.infrastructure.seeder;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserRole;
import com.brokerx.auth_service.domain.model.UserStatus;
import com.brokerx.auth_service.infrastructure.persistence.repository.user.UserRepositoryAdapter;

@Configuration
public class UserSeeder {

        private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    @Bean
    CommandLineRunner seedUser(UserRepositoryAdapter userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                User admin = User.builder()
                        .firstname("Admin")
                        .lastname("User")
                        .email("admin@gmail.com")
                        .password(encoder.encode("Test1234!"))
                        .phoneNumber("+15145551234")
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .address("123 Rue Principale")
                        .city("Montréal")
                        .postalCode("H2X1A4")
                        .role(UserRole.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(admin);
                log.info("Admin user created: {}", admin.getEmail());
            }
        };
    }
}
