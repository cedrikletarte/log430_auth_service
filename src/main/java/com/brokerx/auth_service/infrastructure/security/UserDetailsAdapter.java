package com.brokerx.auth_service.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;

import com.brokerx.auth_service.domain.model.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;

public class UserDetailsAdapter implements UserDetails {

    private final User user;

    /**
     * Constructs a UserDetailsAdapter that adapts a domain User to Spring Security's UserDetails interface.
     */
    public UserDetailsAdapter(User user) {
        this.user = user;
    }

    /**
     * Returns the user's unique identifier from the domain model.
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Returns the user's authorities based on their role for Spring Security authorization.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    /**
     * Returns the user's password for Spring Security authentication.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the user's email as the username for Spring Security authentication.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Returns true indicating that user accounts do not expire in this system.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Returns true indicating that user accounts are not locked in this system.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Returns true indicating that user credentials do not expire in this system.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Returns true indicating that all user accounts are enabled in this system.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
