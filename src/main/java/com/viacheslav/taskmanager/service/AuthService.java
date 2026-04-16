package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.dto.auth.AuthResponse;
import com.viacheslav.taskmanager.model.dto.auth.LoginRequest;
import com.viacheslav.taskmanager.model.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);
}
