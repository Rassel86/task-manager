package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.UserCreateDto;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.model.dto.user.UserUpdateRequest;

import java.util.UUID;

public interface UserAccountService {

    UserResponse getUserAccountById(UUID id);

    UserResponse createUserAccount(UserCreateDto request);

    UserResponse updateUserAccount(UUID userAccountId, UserUpdateRequest request);

    void deleteUserAccount(UUID userAccountId);

    void changePassword(UUID userAccountId, ChangePasswordRequest request);
}
