package com.viacheslav.taskmanager.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ResetPasswordRequest(
        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$",
                message = "Password must contain at least 8 characters, one digit, one lowercase, one uppercase, and one special character")
        String newPassword
) {
}
