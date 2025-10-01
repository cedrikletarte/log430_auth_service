package com.brokerx.auth_service.infrastructure.persistence.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.brokerx.auth_service.application.port.out.OtpCachePort;

@Component
public class RedisOtpCacheAdapter implements OtpCachePort {

    private static final String OTP_KEY_PREFIX = "otp:";
    
    private final StringRedisTemplate redisTemplate;

    /**
     * Constructs a RedisOtpCacheAdapter with the Redis template for OTP cache operations.
     */
    public RedisOtpCacheAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores an OTP code in Redis cache with the specified time-to-live duration.
     */
    @Override
    public void storeOtp(String email, String otpCode, Duration ttl) {
        String key = buildKey(email);
        redisTemplate.opsForValue().set(key, otpCode, ttl);
    }

    /**
     * Retrieves an OTP code from Redis cache for the specified email address.
     */
    @Override
    public Optional<String> getOtp(String email) {
        String key = buildKey(email);
        String otp = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(otp);
    }

    /**
     * Removes an OTP code from Redis cache for the specified email address.
     */
    @Override
    public void removeOtp(String email) {
        String key = buildKey(email);
        redisTemplate.delete(key);
    }

    /**
     * Gets the remaining time in seconds before the OTP expires for the specified email.
     */
    @Override
    public long getRemainingTimeSeconds(String email) {
        String key = buildKey(email);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire != null && expire > 0 ? expire : 0;
    }

    /**
     * Checks if an OTP code exists in Redis cache for the specified email address.
     */
    @Override
    public boolean hasOtp(String email) {
        String key = buildKey(email);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Builds the Redis key for storing OTP by prefixing the email with the OTP key prefix.
     */
    private String buildKey(String email) {
        return OTP_KEY_PREFIX + email;
    }
}