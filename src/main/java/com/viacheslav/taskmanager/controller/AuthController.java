package com.viacheslav.taskmanager.controller;

import com.viacheslav.taskmanager.dto.auth.AuthResponse;
import com.viacheslav.taskmanager.dto.auth.LoginRequest;
import com.viacheslav.taskmanager.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.dto.user.UserResponse;
import com.viacheslav.taskmanager.exception.InvalidTokenException;
import com.viacheslav.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        UserResponse registeredUser = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            throw new InvalidTokenException("Invalid token");
        }

        String refreshToken = authorizationHeader.substring(7);
        AuthResponse response = authService.refreshToken(refreshToken);
        log.info("Received refresh token request");
        return ResponseEntity
                .ok(response);
    }
}
