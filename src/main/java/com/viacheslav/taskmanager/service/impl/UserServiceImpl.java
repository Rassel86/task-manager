package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.dto.PageResponse;
import com.viacheslav.taskmanager.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.dto.user.*;
import com.viacheslav.taskmanager.entity.User;
import com.viacheslav.taskmanager.entity.enums.UserRole;
import com.viacheslav.taskmanager.exception.AccessDeniedException;
import com.viacheslav.taskmanager.exception.PasswordsDontMatchException;
import com.viacheslav.taskmanager.exception.ResourceAlreadyExistsException;
import com.viacheslav.taskmanager.exception.ResourceNotFoundException;
import com.viacheslav.taskmanager.mapper.UserMapper;
import com.viacheslav.taskmanager.repository.UserRepository;
import com.viacheslav.taskmanager.service.UserService;
import com.viacheslav.taskmanager.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserSpecification userSpecification;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserById(UUID id) {
        User user = getUserEntityById(id);
        UserResponse response = userMapper.toUserResponse(user);
        log.info("Successfully retrieved user with ID: {}", response.id());
        return response;
    }

    @Override
    public PageResponse<UserResponse> getUsersPage(UserFilterRequest filter) {
        PageRequest request = PageRequest.of(filter.page(), filter.size(),
                Sort.by(
                        Sort.Direction.fromString(filter.sortDirection()),
                        filter.sortField()
                )
        );

        Specification<User> spec = userSpecification.getUsersSpecification(filter);

        Page<User> usersPage = userRepository.findAll(spec, request);
        Page<UserResponse> responsePage = usersPage.map(userMapper::toUserResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).
                orElseThrow(() -> new ResourceNotFoundException("User with email not found!"));
        return userMapper.toUserResponse(user);
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameIgnoreCase(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("User not found: %s", usernameOrEmail))));
    }

    @Override
    @Transactional
    public UserResponse createUserByAdmin(UserCreateRequest request) {
        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .passwordHash(request.password())
                .email(request.email())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with username {}", savedUser.getUsername());
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse createRegisteredUser(UserCreateRequest request) {
        log.info("Create user via registration: {}", request.email());
        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(request.password())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUserByAdmin(UUID id, UserUpdateByAdminRequest request) {
        User user = getUserEntityById(id);

        if (user.getRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot modify another ADMIN");
        }

        if (request.role() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot assign ADMIN role");
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }

        applyBasicChanges(user,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName());

        User updatedUser = userRepository.save(user);
        log.info("User updated by admin successfully: {}", updatedUser.getEmail());
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(User user, UserUpdateRequest request) {
        applyBasicChanges(user,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getEmail());
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
        log.info("User deleted successfully with id={}", id);
    }

    @Override
    public void blockUser(UUID id) {
        User user = getUserEntityById(id);

        if (user.getRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot block another ADMIN");
        }

        if (!user.isEnabled()) {
            throw new IllegalStateException("User is already blocked");
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(UUID id) {
        User user = getUserEntityById(id);

        if (user.isEnabled()) {
            throw new IllegalArgumentException("User is already unblocked");
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        validateNewPassword(user, request);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPasswordByAdmin(UUID id, String newPassword) {
        User user = getUserEntityById(id);

        if (user.getRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot reset password of another ADMIN");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id=%s not found", id)));
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceAlreadyExistsException(
                    String.format("User with username %s already exists", username));
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException(
                    String.format("User with email %s already exists", email));
        }
    }

    private void validateNewPassword(User user, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordsDontMatchException("New password and confirmation mismatch");
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }
    }

    private void applyBasicChanges(User user, String username, String email, String firstName, String lastName) {
        if (username != null && !username.isBlank()
            && !username.equalsIgnoreCase(user.getUsername())) {
            validateUniqueUsername(username);
            user.setUsername(username);
        }

        if (email != null && !email.isBlank() &&
            !email.equalsIgnoreCase(user.getEmail())) {
            validateUniqueEmail(email);
            user.setEmail(email);
        }

        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }
    }
}
