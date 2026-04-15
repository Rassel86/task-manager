package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.model.User;
import com.viacheslav.taskmanager.repository.UserRepository;
import com.viacheslav.taskmanager.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SimpleCurrentUserService implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        return userRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(this::createDefaultUser);
    }

    @Override
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private User createDefaultUser() {
        User defaultUser = User.builder()
                .firstName("Test")
                .lastName("Testov")
                .username("testuser")
                .email("test@example.com")
                .build();
        return userRepository.save(defaultUser);
    }
}
