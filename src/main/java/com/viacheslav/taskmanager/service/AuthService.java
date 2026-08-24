package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.dto.auth.AuthResult;
import com.viacheslav.taskmanager.model.dto.auth.LoginRequest;
import com.viacheslav.taskmanager.model.dto.auth.RefreshTokenResponse;
import com.viacheslav.taskmanager.model.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResult login(LoginRequest request);

    RefreshTokenResponse refreshAccessToken(String refreshToken);
}
