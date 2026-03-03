package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.dto.UserCreateRequest;
import com.viacheslav.taskmanager.dto.UserResponse;
import com.viacheslav.taskmanager.dto.UserUpdateRequest;
import com.viacheslav.taskmanager.entity.User;
import com.viacheslav.taskmanager.exception.BusinessLogicException;
import com.viacheslav.taskmanager.exception.UserNotFoundException;
import com.viacheslav.taskmanager.mapper.UserMapper;
import com.viacheslav.taskmanager.repository.UserRepository;
import com.viacheslav.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(String id) {
        User user = getUserEntityById(id);
        UserResponse response = userMapper.toUserResponse(user);
        log.info("Successfully retrieved user with ID: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        validateUniqueUsername(request.username(), null);
        validateUniqueEmail(request.email(), null);

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with username {}", savedUser.getUsername());
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse update(String id, UserUpdateRequest request) {
        User user = getUserEntityById(id);

        updateUsernameIfChanged(user, request.username());
        updateEmailIfChanged(user, request.email());

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id {}", updatedUser.getId());
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void delete(String id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
        log.info("User deleted successfully with username {}", user.getUsername());
    }

    private User getUserEntityById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id=%s not found", id)));
    }

    private void validateUniqueUsername(String username, UUID id) {
        boolean exists = id == null
                ? userRepository.existsByEmail(username)
                : userRepository.existsByUsernameAndIdNot(username, id);
        if (exists) {
            throw new BusinessLogicException(
                    String.format("User with username %s already exists", username));
        }
    }

    private void validateUniqueEmail(String email, UUID id) {
        boolean exists = id == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, id);
        if (exists) {
            throw new BusinessLogicException(
                    String.format("User with email %s already exists", email));
        }
    }

    private void updateUsernameIfChanged(User user, String username) {
        if (username != null && !username.equals(user.getUsername())) {
            validateUniqueUsername(username, user.getId());
            user.setUsername(username);
        }
    }

    private void updateEmailIfChanged(User user, String email) {
        if (email != null && !email.equals(user.getEmail())) {
            validateUniqueEmail(email, user.getId());
            user.setEmail(email);
        }
    }
}
