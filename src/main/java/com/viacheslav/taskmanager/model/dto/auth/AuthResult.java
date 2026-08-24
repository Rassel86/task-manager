package com.viacheslav.taskmanager.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AuthResult(
        String accessToken,
        String refreshToken,
        String tokenType,
        UUID userId,
        String username,
        String email,
        String role
) {
}
