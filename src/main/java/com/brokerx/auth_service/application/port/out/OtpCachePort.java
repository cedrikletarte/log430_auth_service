package com.brokerx.auth_service.application.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * Port for OTP caching operations.
 * Abstracts the caching mechanism from the application layer.
 */
public interface OtpCachePort {
    
    /**
     * Store an OTP code with expiration time
     */
    void storeOtp(String email, String otpCode, Duration ttl);
    
    /**
     * Retrieve an OTP code if it exists and is not expired
     */
    Optional<String> getOtp(String email);
    
    /**
     * Remove an OTP code from cache
     */
    void removeOtp(String email);
    
    /**
     * Get remaining time before OTP expiration
     * @return remaining seconds, or 0 if expired/not found
     */
    long getRemainingTimeSeconds(String email);
    
    /**
     * Check if an OTP exists for the given email
     */
    boolean hasOtp(String email);
}