package com.viacheslav.taskmanager.security.service;

import com.viacheslav.taskmanager.repository.CredentialsRepository;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CredentialsRepository credentialsRepository;

    @Override
    public CurrentUser loadUserByUsername(String login) throws UsernameNotFoundException {
        return credentialsRepository.findByLogin(login)
                .map(CurrentUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserAccount not found"));
    }
}
