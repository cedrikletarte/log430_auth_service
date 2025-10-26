package com.brokerx.auth_service.infrastructure.seeder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private static final int TEST_USERS_COUNT = 50;

    @Bean
    CommandLineRunner seedUser(UserRepositoryAdapter userRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            
            // 1. Créer l'admin
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .firstname("Admin")
                        .lastname("Admin")
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
                log.info("✅ Admin user created: {}", admin.getEmail());
            }

            // 2. Créer des utilisateurs de test pour K6
            List<User> testUsers = new ArrayList<>();
            for (int i = 1; i <= TEST_USERS_COUNT; i++) {
                String email = String.format("trader%d@test.com", i);
                
                if (userRepository.findByEmail(email).isEmpty()) {
                    User trader = User.builder()
                            .firstname("Trader")
                            .lastname("Test" + i)
                            .email(email)
                            .password(encoder.encode("Test1234!")) // Même mot de passe pour simplifier
                            .phoneNumber(String.format("+15145550%03d", i))
                            .dateOfBirth(LocalDate.of(1990 + (i % 30), 1 + (i % 12), 1 + (i % 28)))
                            .address(String.format("%d Rue du Commerce", 100 + i))
                            .city("Montréal")
                            .postalCode(String.format("H%dX%dA%d", (i % 9) + 1, (i % 9) + 1, (i % 9) + 1))
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build();
                    testUsers.add(trader);
                }
            }
            
            if (!testUsers.isEmpty()) {
                userRepository.saveAll(testUsers); // Utilise saveAll pour le batching!
                log.info("✅ Created {} test users (trader1@test.com to trader{}@test.com)", 
                         testUsers.size(), TEST_USERS_COUNT);
            } else {
                log.info("ℹ️ Test users already exist");
            }
        };
    }
}