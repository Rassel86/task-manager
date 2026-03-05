package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.dto.UserCreateRequest;
import com.viacheslav.taskmanager.dto.UserPatchRequest;
import com.viacheslav.taskmanager.dto.UserResponse;
import com.viacheslav.taskmanager.dto.UserUpdateRequest;

import java.util.UUID;

public interface UserService {

    UserResponse getUserById(UUID id);

    UserResponse createUser(UserCreateRequest request);

    UserResponse patchUser(UUID id, UserPatchRequest request);

    UserResponse updateUser(UUID id, UserUpdateRequest request);

    void deleteUser(UUID id);
}
