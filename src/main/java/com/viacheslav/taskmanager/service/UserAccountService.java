package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.PageResponse;
import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.*;

import java.util.UUID;

public interface UserAccountService {

    UserResponse getUserById(UUID id);

    PageResponse<UserResponse> getUsersPage(UserFilterRequest filter);

    UserResponse updateUserByAdmin(UUID id, UserUpdateByAdminRequest request);

    UserResponse updateUser(UserAccount userAccount, UserUpdateRequest request);

    void deleteUser(UUID id);

    void deleteUserByAdmin(UUID id);

    void changePassword(UserAccount userAccount, ChangePasswordRequest request);

    UserResponse createRegisteredUser(UserCreateRequest request);

    UserAccount findByUsernameOrEmail(String usernameOrEmail);

    UserResponse getByEmail(String email);

    UserResponse createUserByAdmin(UserCreateRequest request);

    void resetPasswordByAdmin(UUID id, String newPassword);

    void blockUser(UUID id);

    void unblockUser(UUID id);
}
