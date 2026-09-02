package com.viacheslav.taskmanager.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateDto(

        @Size(max = 50, message = "First name cannot exceed {max} characters")
        @Pattern(regexp = "^[A-Za-z\\-]+$",
                message = "First name can only contain letters and hyphens (e.g., Anna-Maria)")
        String firstName,

        @Size(max = 50, message = "Last name cannot exceed {max} characters")
        @Pattern(regexp = "^[A-Za-z\\-]+$",
                message = "Last name can only contain letters and hyphens (e.g., Smith-Jones)")
        String lastName,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username can only contain: letters (a-z), numbers, dots (.), underscores (_) and hyphens (-)")
        String displayName,

        @NotBlank(message = "Email is required")
        @Size(max = 100, message = "Email is too long")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$",
                message = "Please provide a valid email address")
        String contactEmail,

        @NotBlank
        String password
) {
}
