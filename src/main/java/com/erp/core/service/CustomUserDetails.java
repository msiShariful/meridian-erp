package com.erp.core.service;

import com.erp.core.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring Security principal backed by the persisted {@link User}.
 * Exposes roles as {@code ROLE_*} authorities and granular permissions as plain authorities.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        Set<GrantedAuthority> auths = new HashSet<>();
        user.getRoles().forEach(role -> {
            auths.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            role.getPermissions().forEach(p -> auths.add(new SimpleGrantedAuthority(p.getName())));
        });
        this.authorities = auths;
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getInitials() {
        return user.getInitials();
    }

    public String getPrimaryRoleDisplay() {
        return user.getPrimaryRoleDisplay();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
