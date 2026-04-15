package com.viacheslav.taskmanager.security.service;

import com.viacheslav.taskmanager.repository.UserRepository;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CurrentUser loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(CurrentUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
