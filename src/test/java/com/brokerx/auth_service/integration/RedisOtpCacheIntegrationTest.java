package com.brokerx.auth_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.brokerx.auth_service.infrastructure.persistence.cache.RedisOtpCacheAdapter;

@Testcontainers
@DataRedisTest
@Import(RedisOtpCacheAdapter.class)
class RedisOtpCacheIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private RedisOtpCacheAdapter otpCacheAdapter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        // Nettoyer Redis avant chaque test
        var connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory != null) {
            connectionFactory.getConnection().serverCommands().flushAll();
        }
    }

    @Test
    void shouldStoreAndRetrieveOtp() {
        String email = "test@example.com";
        String otpCode = "123456";
        Duration ttl = Duration.ofMinutes(5);

        otpCacheAdapter.storeOtp(email, otpCode, ttl);

        Optional<String> retrieved = otpCacheAdapter.getOtp(email);
        assertTrue(retrieved.isPresent());
        assertEquals(otpCode, retrieved.get());
    }

    @Test
    void shouldReturnEmptyWhenOtpNotFound() {
        String email = "nonexistent@example.com";

        Optional<String> retrieved = otpCacheAdapter.getOtp(email);
        assertFalse(retrieved.isPresent());
    }

    @Test
    void shouldRemoveOtp() {
        String email = "remove@example.com";
        String otpCode = "654321";
        Duration ttl = Duration.ofMinutes(5);

        otpCacheAdapter.storeOtp(email, otpCode, ttl);
        assertTrue(otpCacheAdapter.hasOtp(email));

        otpCacheAdapter.removeOtp(email);
        assertFalse(otpCacheAdapter.hasOtp(email));
    }

    @Test
    void shouldCheckIfOtpExists() {
        String email = "exists@example.com";
        String otpCode = "111111";
        Duration ttl = Duration.ofMinutes(5);

        assertFalse(otpCacheAdapter.hasOtp(email));

        otpCacheAdapter.storeOtp(email, otpCode, ttl);
        assertTrue(otpCacheAdapter.hasOtp(email));
    }

    @Test
    void shouldGetRemainingTimeSeconds() throws InterruptedException {
        String email = "time@example.com";
        String otpCode = "222222";
        Duration ttl = Duration.ofSeconds(10);

        otpCacheAdapter.storeOtp(email, otpCode, ttl);

        long remainingTime = otpCacheAdapter.getRemainingTimeSeconds(email);
        assertTrue(remainingTime > 0 && remainingTime <= 10, 
                "Remaining time should be between 1 and 10 seconds, but was: " + remainingTime);

        // Attendre 2 secondes
        Thread.sleep(2000);

        long remainingTimeAfter = otpCacheAdapter.getRemainingTimeSeconds(email);
        assertTrue(remainingTimeAfter < remainingTime, 
                "Remaining time should decrease after waiting");
    }

    @Test
    void shouldReturnZeroRemainingTimeForNonExistentOtp() {
        String email = "notime@example.com";

        long remainingTime = otpCacheAdapter.getRemainingTimeSeconds(email);
        assertEquals(0, remainingTime);
    }

    @Test
    void shouldExpireOtpAfterTtl() throws InterruptedException {
        String email = "expire@example.com";
        String otpCode = "333333";
        Duration ttl = Duration.ofSeconds(2);

        otpCacheAdapter.storeOtp(email, otpCode, ttl);
        assertTrue(otpCacheAdapter.hasOtp(email));

        // Attendre que le OTP expire
        Thread.sleep(3000);

        assertFalse(otpCacheAdapter.hasOtp(email));
        Optional<String> retrieved = otpCacheAdapter.getOtp(email);
        assertFalse(retrieved.isPresent());
    }

    @Test
    void shouldOverwriteExistingOtp() {
        String email = "overwrite@example.com";
        String oldOtp = "444444";
        String newOtp = "555555";
        Duration ttl = Duration.ofMinutes(5);

        otpCacheAdapter.storeOtp(email, oldOtp, ttl);
        assertEquals(oldOtp, otpCacheAdapter.getOtp(email).get());

        otpCacheAdapter.storeOtp(email, newOtp, ttl);
        assertEquals(newOtp, otpCacheAdapter.getOtp(email).get());
    }

    @Test
    void shouldHandleMultipleUsersSimultaneously() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String email3 = "user3@example.com";
        
        String otp1 = "111111";
        String otp2 = "222222";
        String otp3 = "333333";
        
        Duration ttl = Duration.ofMinutes(5);

        otpCacheAdapter.storeOtp(email1, otp1, ttl);
        otpCacheAdapter.storeOtp(email2, otp2, ttl);
        otpCacheAdapter.storeOtp(email3, otp3, ttl);

        assertEquals(otp1, otpCacheAdapter.getOtp(email1).get());
        assertEquals(otp2, otpCacheAdapter.getOtp(email2).get());
        assertEquals(otp3, otpCacheAdapter.getOtp(email3).get());

        assertTrue(otpCacheAdapter.hasOtp(email1));
        assertTrue(otpCacheAdapter.hasOtp(email2));
        assertTrue(otpCacheAdapter.hasOtp(email3));
    }

    @Test
    void shouldHandleDifferentTtlForDifferentUsers() throws InterruptedException {
        String email1 = "short@example.com";
        String email2 = "long@example.com";
        
        String otp1 = "111111";
        String otp2 = "222222";

        otpCacheAdapter.storeOtp(email1, otp1, Duration.ofSeconds(2));
        otpCacheAdapter.storeOtp(email2, otp2, Duration.ofMinutes(5));

        assertTrue(otpCacheAdapter.hasOtp(email1));
        assertTrue(otpCacheAdapter.hasOtp(email2));

        // Attendre que le premier OTP expire
        Thread.sleep(3000);

        assertFalse(otpCacheAdapter.hasOtp(email1));
        assertTrue(otpCacheAdapter.hasOtp(email2));
    }

    @Test
    void shouldRemoveSpecificOtpWithoutAffectingOthers() {
        String email1 = "keep@example.com";
        String email2 = "remove@example.com";
        
        String otp1 = "111111";
        String otp2 = "222222";
        
        Duration ttl = Duration.ofMinutes(5);

        otpCacheAdapter.storeOtp(email1, otp1, ttl);
        otpCacheAdapter.storeOtp(email2, otp2, ttl);

        otpCacheAdapter.removeOtp(email2);

        assertTrue(otpCacheAdapter.hasOtp(email1));
        assertFalse(otpCacheAdapter.hasOtp(email2));
        assertEquals(otp1, otpCacheAdapter.getOtp(email1).get());
    }
}
