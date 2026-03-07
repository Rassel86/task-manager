package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.dto.UserCreateRequest;
import com.viacheslav.taskmanager.dto.UserPatchRequest;
import com.viacheslav.taskmanager.dto.UserResponse;
import com.viacheslav.taskmanager.dto.UserUpdateRequest;
import com.viacheslav.taskmanager.entity.User;
import com.viacheslav.taskmanager.exception.BusinessLogicException;
import com.viacheslav.taskmanager.exception.ResourceNotFoundException;
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
    public UserResponse getUserById(UUID id) {
        User user = getUserEntityById(id);
        UserResponse response = userMapper.toUserResponse(user);
        log.info("Successfully retrieved user with ID: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
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
    public UserResponse patchUser(UUID id, UserPatchRequest request) {
        User user = getUserEntityById(id);

        updateUsernameIfChanged(user, request.username());
        updateEmailIfChanged(user, request.email());

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        User patchedUser = userRepository.save(user);
        log.info("User updated successfully with id {}", patchedUser.getId());
        return userMapper.toUserResponse(patchedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = getUserEntityById(id);

        if (!request.username().equals(user.getUsername())) {
            validateUniqueEmail(request.email(), id);
        }

        if (!request.email().equals(user.getEmail())) {
            validateUniqueEmail(request.email(), id);
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
        log.info("User deleted successfully with id={}", id);
    }

    private User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id=%s not found", id)));
    }

    private void validateUniqueUsername(String username, UUID id) {
        boolean exists = id == null
                ? userRepository.existsByUsername(username)
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
