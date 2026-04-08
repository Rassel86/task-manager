package com.viacheslav.taskmanager.dto.auth;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UUID userId,
        String username,
        String email,
        String role
) {
}
