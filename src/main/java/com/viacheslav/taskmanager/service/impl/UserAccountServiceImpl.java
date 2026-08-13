package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.*;
import com.viacheslav.taskmanager.mapper.UserMapper;
import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.PageResponse;
import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.*;
import com.viacheslav.taskmanager.model.enums.UserRole;
import com.viacheslav.taskmanager.repository.UserAccountRepository;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import com.viacheslav.taskmanager.service.UserAccountService;
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
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserMapper userMapper;
    private final UserSpecification userSpecification;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserById(UUID id) {
        UserAccount userAccount = getUserEntityById(id);
        UserResponse response = userMapper.toUserResponse(userAccount);
        log.info("Successfully retrieved userAccount with ID: {}", response.id());
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

        Specification<UserAccount> spec = userSpecification.getUsersSpecification(filter);

        Page<UserAccount> usersPage = userAccountRepository.findAll(spec, request);
        Page<UserResponse> responsePage = usersPage.map(userMapper::toUserResponse);
        log.info("Retrieved {} users out of {}", responsePage.getContent().size(), responsePage.getTotalPages());
        return PageResponse.from(responsePage);
    }

    @Override
    public UserResponse getByEmail(String email) {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email).
                orElseThrow(() -> new ResourceNotFoundException("UserAccount with email not found!"));
        return userMapper.toUserResponse(userAccount);
    }

    @Override
    public UserAccount findByUsernameOrEmail(String usernameOrEmail) {
        return userAccountRepository.findByUsernameIgnoreCase(usernameOrEmail)
                .orElseGet(() -> userAccountRepository.findByEmailIgnoreCase(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("UserAccount not found: %s", usernameOrEmail))));
    }

    @Override
    @Transactional
    public UserResponse createUserByAdmin(UserCreateRequest request) {
        log.info("Admin updating userAccount with email {} and username {}",
                LoggingUtils.maskEmail(request.email()), LoggingUtils.maskUsername(request.username()));
        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());

        UserAccount userAccount = UserAccount.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .displayName(request.username())
                .contactEmail(request.email())
                .build();

        UserAccount savedUserAccount = userAccountRepository.save(userAccount);
        log.info("UserAccount created successfully with username {}", LoggingUtils.maskUsername(savedUserAccount.getDisplayName()));
        return userMapper.toUserResponse(savedUserAccount);
    }

    @Override
    @Transactional
    public UserResponse createRegisteredUser(UserCreateRequest request) {
        log.info("Create userAccount via registration: {}", LoggingUtils.maskEmail(request.email()));
        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());

        UserAccount userAccount = UserAccount.builder()
                .displayName(request.username())
                .contactEmail(request.email())
                .build();

        UserAccount savedUserAccount = userAccountRepository.save(userAccount);
        log.info("UserAccount created successfully with id: {}", savedUserAccount.getId());
        return userMapper.toUserResponse(savedUserAccount);
    }

    @Override
    @Transactional
    public UserResponse updateUserByAdmin(UUID id, UserUpdateByAdminRequest request) {
        log.info("Admin update attempt userAccount with ID: {}", id);
        UserAccount userAccount = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(userAccount);

        if (request.role() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot assign ADMIN role");
        }

        if (request.role() != null) {
            userAccount.setRole(request.role());
        }

        if (request.enabled() != null) {
            userAccount.setEnabled(request.enabled());
        }

        applyBasicChanges(userAccount,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName());

        UserAccount updatedUserAccount = userAccountRepository.save(userAccount);
        log.info("UserAccount updated by admin successfully: {}", LoggingUtils.maskEmail(updatedUserAccount.getContactEmail()));
        return userMapper.toUserResponse(userAccount);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserAccount userAccount, UserUpdateRequest request) {
        log.info("Updating userAccount: {} ({})", LoggingUtils.maskEmail(userAccount.getContactEmail()), userAccount.getId());

        applyBasicChanges(userAccount,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName());

        UserAccount updatedUserAccount = userAccountRepository.save(userAccount);
        log.info("UserAccount updated successfully: {}", LoggingUtils.maskEmail(updatedUserAccount.getContactEmail()));
        return userMapper.toUserResponse(updatedUserAccount);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.info("Attempting to delete userAccount with ID {} ", id);
        UserAccount userAccount = getUserEntityById(id);
        userAccountRepository.delete(userAccount);
        log.info("UserAccount with ID {} deleted successfully", id);
    }

    @Override
    @Transactional
    public void deleteUserByAdmin(UUID id) {
        log.info("Admin attempting userAccount with ID {}", id);
        UserAccount userAccount = getUserEntityById(id);
        validateAdminCannotModifySelfOrOtherAdmin(userAccount);
        userAccountRepository.delete(userAccount);
        log.info("Admin successfully deleted userAccount with ID {}", id);
    }

    @Override
    public void blockUser(UUID id) {
        log.warn("Blocking userAccount with ID: {}", id);
        UserAccount userAccount = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(userAccount);

        if (!userAccount.isEnabled()) {
            throw new UserAlreadyBlockedException("UserAccount is already blocked");
        }

        userAccount.setEnabled(false);
        userAccountRepository.save(userAccount);
        log.info("UserAccount with ID {} blocked successfully", id);
    }

    @Override
    public void unblockUser(UUID id) {
        log.warn("Unblocking userAccount with ID: {}", id);
        UserAccount userAccount = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(userAccount);

        if (userAccount.isEnabled()) {
            throw new UserAlreadyUnblockedException("UserAccount is already unblocked");
        }

        userAccount.setEnabled(true);
        userAccountRepository.save(userAccount);
        log.info("UserAccount with ID {} unblocked successfully", id);
    }

    @Override
    @Transactional
    public void changePassword(UserAccount userAccount, ChangePasswordRequest request) {
        log.info("UserAccount with ID {} attempt change password", userAccount.getId());
        validateNewPassword(userAccount, request);
//        userAccount.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userAccountRepository.save(userAccount);
        log.info("UserAccount with ID {} successfully changed password", userAccount.getId());
    }

    @Override
    @Transactional
    public void resetPasswordByAdmin(UUID id, String newPassword) {
        log.info("Admin attempting reset password for userAccount with ID: {}", id);
        UserAccount userAccount = getUserEntityById(id);

        validateAdminCannotModifySelfOrOtherAdmin(userAccount);

//        if (passwordEncoder.matches(newPassword, userAccount.getPasswordHash())) {
//            throw new PasswordSameAsOldException("New password must be different from the current password");
//        }

//        userAccount.setPasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(userAccount);
        log.info("Admin successfully reset password for userAccount with ID: {}", id);
    }

    private UserAccount getUserEntityById(UUID id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("UserAccount with ID %s not found", id)));
    }

    private void validateUniqueUsername(String username) {
        if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceAlreadyExistsException("Username is already taken");
        }
    }

    private void validateUniqueEmail(String email) {
        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException("Email is already taken");
        }
    }

    private void validateNewPassword(UserAccount userAccount, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordsDontMatchException("New password and confirmation mismatch");
        }

//        if (!passwordEncoder.matches(request.oldPassword(), userAccount.getPasswordHash())) {
//            throw new BadCredentialsException("Current password is incorrect");
//        }
//
//        if (passwordEncoder.matches(request.newPassword(), userAccount.getPasswordHash())) {
//            throw new PasswordSameAsOldException("New password must be different from current password");
//        }
    }

    private void applyBasicChanges(UserAccount userAccount, String username, String email, String firstName, String lastName) {
        if (username != null && !username.isBlank()
            && !username.equalsIgnoreCase(userAccount.getDisplayName())) {
            validateUniqueUsername(username);
            userAccount.setDisplayName(username);
        }

        if (email != null && !email.isBlank() &&
            !email.equalsIgnoreCase(userAccount.getContactEmail())) {
            validateUniqueEmail(email);
            userAccount.setContactEmail(email);
        }

        if (firstName != null && !firstName.isBlank()) {
            userAccount.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            userAccount.setLastName(lastName);
        }
    }

    private void validateAdminCannotModifySelfOrOtherAdmin(UserAccount targetUserAccount) {
        UserAccount currentAdmin = getCurrentUser();

        if (currentAdmin.getId().equals(targetUserAccount.getId())) {
            log.warn("Admin {} attempted to modify their own account", LoggingUtils.maskEmail(currentAdmin.getContactEmail()));
            throw new AccessDeniedException("Cannot modify your own account. Use profile endpoint");
        }

        if (targetUserAccount.getRole() == UserRole.ADMIN) {
            log.warn("Admin {} attempted to modify another ADMIN: {}",
                    LoggingUtils.maskEmail(currentAdmin.getContactEmail()), LoggingUtils.maskEmail(targetUserAccount.getContactEmail()));
            throw new AccessDeniedException("Cannot modify another ADMIN");
        }
    }

    private UserAccount getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (!(principal instanceof CurrentUser currentUser)) {
            throw new IllegalStateException("Unexpected principal type: " +
                                            principal.getClass().getSimpleName());
        }

        return currentUser.getCredentials().getUserAccount();
    }
}
