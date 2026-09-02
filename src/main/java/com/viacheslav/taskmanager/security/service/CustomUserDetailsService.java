package com.viacheslav.taskmanager.security.service;

import com.viacheslav.taskmanager.repository.CredentialsRepository;
import com.viacheslav.taskmanager.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CredentialsRepository credentialsRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return credentialsRepository.findByLoginWithUserAccount(login)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserAccount not found"));
    }
}
