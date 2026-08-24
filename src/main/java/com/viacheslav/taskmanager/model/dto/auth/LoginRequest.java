package com.viacheslav.taskmanager.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password
) {
}
