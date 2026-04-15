package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.*;
import com.viacheslav.taskmanager.mapper.UserMapper;
import com.viacheslav.taskmanager.model.User;
import com.viacheslav.taskmanager.model.dto.PageResponse;
import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.*;
import com.viacheslav.taskmanager.model.enums.UserRole;
import com.viacheslav.taskmanager.repository.UserRepository;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import com.viacheslav.taskmanager.service.UserService;
import com.viacheslav.taskmanager.specification.UserSpecification;
import com.viacheslav.taskmanager.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Fetching users page with filter: {}", filter);
        PageRequest request = PageRequest.of(filter.page(), filter.size(),
                Sort.by(
                        Sort.Direction.fromString(filter.sortDirection()),
                        filter.sortField()
                )
        );

        Specification<User> spec = userSpecification.getUsersSpecification(filter);

        Page<User> usersPage = userRepository.findAll(spec, request);
        Page<UserResponse> responsePage = usersPage.map(userMapper::toUserResponse);
        log.info("Retrieved {} users out of {}", responsePage.getContent().size(), responsePage.getTotalPages());
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
        log.info("Admin updating user with email {} and username {}",
                LoggingUtils.maskEmail(request.email()), LoggingUtils.maskUsername(request.username()));
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
        log.info("User created successfully with username {}", LoggingUtils.maskUsername(savedUser.getUsername()));
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse createRegisteredUser(UserCreateRequest request) {
        log.info("Create user via registration: {}", LoggingUtils.maskEmail(request.email()));
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
        log.info("Admin update attempt user with ID: {}", id);
        User user = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(user);

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
        log.info("User updated by admin successfully: {}", LoggingUtils.maskEmail(updatedUser.getEmail()));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(User user, UserUpdateRequest request) {
        log.info("Updating user: {} ({})", LoggingUtils.maskEmail(user.getEmail()), user.getId());

        applyBasicChanges(user,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", LoggingUtils.maskEmail(updatedUser.getEmail()));
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.info("Attempting to delete user with ID {} ", id);
        User user = getUserEntityById(id);
        userRepository.delete(user);
        log.info("User with ID {} deleted successfully", id);
    }

    @Override
    @Transactional
    public void deleteUserByAdmin(UUID id) {
        log.info("Admin attempting user with ID {}", id);
        User user = getUserEntityById(id);
        validateAdminCannotModifySelfOrOtherAdmin(user);
        userRepository.delete(user);
        log.info("Admin successfully deleted user with ID {}", id);
    }

    @Override
    public void blockUser(UUID id) {
        log.warn("Blocking user with ID: {}", id);
        User user = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(user);

        if (!user.isEnabled()) {
            throw new UserAlreadyBlockedException("User is already blocked");
        }

        user.setEnabled(false);
        userRepository.save(user);
        log.info("User with ID {} blocked successfully", id);
    }

    @Override
    public void unblockUser(UUID id) {
        log.warn("Unblocking user with ID: {}", id);
        User user = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(user);

        if (user.isEnabled()) {
            throw new UserAlreadyUnblockedException("User is already unblocked");
        }

        user.setEnabled(true);
        userRepository.save(user);
        log.info("User with ID {} unblocked successfully", id);
    }

    @Override
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        log.info("User with ID {} attempt change password", user.getId());
        validateNewPassword(user, request);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("User with ID {} successfully changed password", user.getId());
    }

    @Override
    @Transactional
    public void resetPasswordByAdmin(UUID id, String newPassword) {
        log.info("Admin attempting reset password for user with ID: {}", id);
        User user = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(user);

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new PasswordSameAsOldException("New password must be different from the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Admin successfully reset password for user with ID: {}", id);
    }

    private User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with ID %s not found", id)));
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceAlreadyExistsException(
                    String.format("User with username %s already exists", LoggingUtils.maskUsername(username)));
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException(
                    String.format("User with email %s already exists", LoggingUtils.maskEmail(email)));
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
            throw new PasswordSameAsOldException("New password must be different from current password");
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

    private void validateAdminCannotModifySelfOrOtherAdmin(User targetUser) {
        User currentAdmin = getCurrentUser();

        if (currentAdmin.getId().equals(targetUser.getId())) {
            log.warn("Admin {} attempted to modify their own account", LoggingUtils.maskEmail(currentAdmin.getEmail()));
            throw new AccessDeniedException("Cannot modify your own account. Use profile endpoint");
        }

        if (targetUser.getRole() == UserRole.ADMIN) {
            log.warn("Admin {} attempted to modify another ADMIN: {}",
                    LoggingUtils.maskEmail(currentAdmin.getEmail()), LoggingUtils.maskEmail(targetUser.getEmail()));
            throw new AccessDeniedException("Cannot modify another ADMIN");
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (!(principal instanceof CurrentUser currentUser)) {
            throw new IllegalStateException("Unexpected principal type: " +
                                            principal.getClass().getSimpleName());
        }

        return currentUser.getUser();
    }
}
