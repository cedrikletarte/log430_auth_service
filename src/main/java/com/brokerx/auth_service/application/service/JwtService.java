package com.brokerx.auth_service.application.service;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.brokerx.auth_service.domain.model.User;

import java.util.Map;
import java.util.function.Function;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    /* Retrieves the signing key for JWT */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /* Extracts all claims from the JWT token */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /* Extracts a specific claim from the JWT token */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    /* Extracts the user ID (subject) from the JWT token */
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject); // `sub` = userId
    }

    /* Extracts the email from the JWT token */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    /* Extracts the role from the JWT token */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /* Checks if the JWT token is expired */
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /* Validates the JWT token */
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    /* Generates a JWT token for the given user with default empty extra claims. */
    public String generateToken(User user) {
        return generateToken(Map.of(), user);
    }

    /* Generates a JWT token for the user with additional custom claims and user information. */
    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .claims(extraClaims)
                .claim("firstName", user.getFirstname())
                .claim("lastName", user.getLastname())
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .subject(user.getId().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }
}
