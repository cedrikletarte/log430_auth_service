package com.brokerx.auth_service.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.brokerx.auth_service.domain.model.User;
import com.brokerx.auth_service.domain.model.UserRole;

class JwtServiceTest {

    private JwtService jwtService;
    
    // Clé secrète de test (en base64)
    private static final String TEST_SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tdGhhdC1pcy1sb25nLWVub3VnaC1mb3ItaHMyNTY=";
    private static final long TEST_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", TEST_EXPIRATION);
    }

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void shouldGenerateToken() {
        User user = createTestUser();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldGenerateTokenWithExtraClaims() {
        User user = createTestUser();
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        String token = jwtService.generateToken(extraClaims, user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractEmailFromToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(user.getEmail(), extractedEmail);
    }

    @Test
    void shouldExtractUsernameFromToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        String extractedUsername = jwtService.extractUsername(token);

        // Le username devrait être l'email
        assertEquals(user.getEmail(), extractedUsername);
    }

    @Test
    void shouldValidateTokenForCorrectUser() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        User user1 = createTestUser();
        User user2 = User.builder()
                .id(2L)
                .firstname("Jane")
                .lastname("Smith")
                .email("jane.smith@example.com")
                .role(UserRole.USER)
                .build();

        String token = jwtService.generateToken(user1);

        boolean isValid = jwtService.isTokenValid(token, user2);

        assertFalse(isValid);
    }

    @Test
    void shouldDetectNonExpiredToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void shouldIncludeUserDetailsInToken() {
        User user = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .role(UserRole.ADMIN)
                .build();

        String token = jwtService.generateToken(user);

        // Extraire et vérifier les claims personnalisés
        String firstName = jwtService.extractClaim(token, claims -> claims.get("firstName", String.class));
        String lastName = jwtService.extractClaim(token, claims -> claims.get("lastName", String.class));
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        String email = jwtService.extractClaim(token, claims -> claims.get("email", String.class));

        assertEquals("John", firstName);
        assertEquals("Doe", lastName);
        assertEquals("ADMIN", role);
        assertEquals("john.doe@example.com", email);
    }

    @Test
    void shouldSetSubjectAsUserId() {
        User user = createTestUser();
        user.setId(123L);

        String token = jwtService.generateToken(user);

        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        assertEquals("123", subject);
    }

    @Test
    void shouldHandleDifferentUserRoles() {
        User adminUser = User.builder()
                .id(1L)
                .firstname("Admin")
                .lastname("User")
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .build();

        User regularUser = User.builder()
                .id(2L)
                .firstname("Regular")
                .lastname("User")
                .email("user@example.com")
                .role(UserRole.USER)
                .build();

        String adminToken = jwtService.generateToken(adminUser);
        String userToken = jwtService.generateToken(regularUser);

        String adminRole = jwtService.extractClaim(adminToken, claims -> claims.get("role", String.class));
        String userRole = jwtService.extractClaim(userToken, claims -> claims.get("role", String.class));

        assertEquals("ADMIN", adminRole);
        assertEquals("USER", userRole);
    }
}
