package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.dto.UserCreateRequest;
import com.viacheslav.taskmanager.dto.UserResponse;
import com.viacheslav.taskmanager.dto.UserUpdateRequest;

public interface UserService {

    UserResponse getUserById(String id);
    UserResponse create(UserCreateRequest request);
    UserResponse update(String id, UserUpdateRequest request);
    void delete(String id);
}
