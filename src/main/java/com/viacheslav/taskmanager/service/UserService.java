package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.User;
import com.viacheslav.taskmanager.model.dto.PageResponse;
import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.*;

import java.util.UUID;

public interface UserService {

    UserResponse getUserById(UUID id);

    PageResponse<UserResponse> getUsersPage(UserFilterRequest filter);

    UserResponse updateUserByAdmin(UUID id, UserUpdateByAdminRequest request);

    UserResponse updateUser(User user, UserUpdateRequest request);

    void deleteUser(UUID id);

    void deleteUserByAdmin(UUID id);

    void changePassword(User user, ChangePasswordRequest request);

    UserResponse createRegisteredUser(UserCreateRequest request);

    User findByUsernameOrEmail(String usernameOrEmail);

    UserResponse getByEmail(String email);

    UserResponse createUserByAdmin(UserCreateRequest request);

    void resetPasswordByAdmin(UUID id, String newPassword);

    void blockUser(UUID id);

    void unblockUser(UUID id);
}
