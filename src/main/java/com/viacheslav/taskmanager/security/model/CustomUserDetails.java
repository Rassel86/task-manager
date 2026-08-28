package com.viacheslav.taskmanager.security.model;

import com.viacheslav.taskmanager.model.Credentials;
import com.viacheslav.taskmanager.model.UserAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Credentials credentials;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(credentials.getUserAccount().getRole().getAuthority()));
    }

    @Override
    public String getPassword() {
        return credentials.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return credentials.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return credentials.isEnabled();
    }

    public UserAccount getUserAccount() {
        return credentials.getUserAccount();
    }

    public UUID getUserId() {
        return credentials.getUserAccount().getId();
    }
}
