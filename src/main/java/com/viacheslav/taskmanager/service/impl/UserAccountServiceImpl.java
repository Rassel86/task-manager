package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.*;
import com.viacheslav.taskmanager.mapper.UserMapper;
import com.viacheslav.taskmanager.model.Credentials;
import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.*;
import com.viacheslav.taskmanager.repository.CredentialsRepository;
import com.viacheslav.taskmanager.repository.UserAccountRepository;
import com.viacheslav.taskmanager.service.FileStorageService;
import com.viacheslav.taskmanager.service.UserAccountService;
import com.viacheslav.taskmanager.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final CredentialsRepository credentialsRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUserAccount(UserCreateDto request) {
        log.info("Create userAccount via registration: {}", LoggingUtils.maskEmail(request.contactEmail()));
        validateUniqueUsername(request.displayName());
        validateUniqueEmail(request.contactEmail());

        UserAccount account = UserAccount.builder()
                .displayName(request.displayName())
                .contactEmail(request.contactEmail())
                .build();

        Credentials credentials = Credentials.builder()
                .login(request.contactEmail())
                .passwordHash(request.password())
                .userAccount(account)
                .build();

        account.setCredentials(credentials);

        UserAccount createdUserAccount = userAccountRepository.save(account);
        log.info("UserAccount created successfully with id: {}", createdUserAccount.getId());
        return userMapper.toUserResponse(createdUserAccount);
    }

    @Override
    @Transactional
    public UserResponse updateUserAccount(UUID userAccountId, UserUpdateRequest request) {
        validateUniqueUsername(request.displayName());
        validateUniqueEmail(request.contactEmail());
        UserAccount userAccount = getUserAccountEntityById(userAccountId);
        log.info("Updating userAccount: {} ({})", LoggingUtils.maskEmail(userAccount.getContactEmail()), userAccountId);
        userMapper.updateUserAccountFromDto(request, userAccount);
        UserAccount updatedUserAccount = userAccountRepository.save(userAccount);
        log.info("UserAccount updated successfully: {}", LoggingUtils.maskEmail(updatedUserAccount.getContactEmail()));
        return userMapper.toUserResponse(updatedUserAccount);
    }

    @Override
    @Transactional
    public void deleteUserAccount(UUID id) {
        log.info("Attempting to delete userAccount with ID {} ", id);
        UserAccount userAccount = getUserAccountEntityById(id);
        userAccountRepository.delete(userAccount);
        log.info("UserAccount with ID {} deleted successfully", id);
    }

    @Override
    public UserResponse getUserAccountById(UUID id) {
        UserAccount userAccount = getUserAccountEntityById(id);
        UserResponse response = userMapper.toUserResponse(userAccount);
        log.info("Successfully retrieved userAccount with ID: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public void changePassword(UUID userAccountId, ChangePasswordRequest request) {
        log.info("UserAccount with ID {} attempt change password", userAccountId);
        UserAccount userAccount = getUserAccountEntityById(userAccountId);
        Credentials credentials = userAccount.getCredentials();
        validateNewPassword(credentials, request);
        credentials.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userAccount.setCredentials(credentials);
        userAccountRepository.save(userAccount);
        log.info("UserAccount with ID {} successfully changed password", userAccountId);
    }

    private UserAccount getUserAccountEntityById(UUID id) {
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
        if (credentialsRepository.existsByLogin(email)) {
            throw new ResourceAlreadyExistsException("Email is already taken");
        }
    }

    private void validateNewPassword(Credentials credentials, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordsDontMatchException("New password and confirmation mismatch");
        }

        if (!passwordEncoder.matches(request.oldPassword(), credentials.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), credentials.getPasswordHash())) {
            throw new PasswordSameAsOldException("New password must be different from current password");
        }
    }
}
