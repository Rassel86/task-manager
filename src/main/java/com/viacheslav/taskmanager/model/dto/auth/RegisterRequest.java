package com.viacheslav.taskmanager.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username can only contain: letters (a-z), numbers, dots (.), underscores (_) and hyphens (-)")
        String displayName,

        @NotBlank(message = "Email is required")
        @Size(max = 100, message = "Email is too long")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$",
                message = "Password must contain at least 8 characters, one digit, one lowercase, one uppercase, and one special character")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {

}
