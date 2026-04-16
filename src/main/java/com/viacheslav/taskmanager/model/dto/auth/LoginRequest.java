package com.viacheslav.taskmanager.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotBlank(message = "Email or username is required")
        String usernameOrEmail,

        @NotBlank(message = "Password is required")
        String password
) {
}
