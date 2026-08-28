package com.viacheslav.taskmanager.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserUpdateRequest(

        @Size(max = 50, message = "First name cannot exceed {max} characters")
        @Pattern(regexp = "^[A-Za-z\\-]+$",
                message = "First name can only contain letters and hyphens (e.g., Anna-Maria)")
        String firstName,

        @Size(max = 50, message = "Last name cannot exceed {max} characters")
        @Pattern(regexp = "^[A-Za-z\\-]+$",
                message = "Last name can only contain letters and hyphens (e.g., Smith-Jones)")
        String lastName,

        @Size(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username can only contain: letters (a-z), numbers, dots (.), underscores (_) and hyphens (-)")
        String displayName,

        @Size(max = 100, message = "Email is too long")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$",
                message = "Please provide a valid email address")
        String contactEmail,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$",
                message = "Invalid phone number format. Use: +1234567890")
        String phoneNumber,

        @Size(max = 100, message = "Company name must not exceed 100 characters")
        @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9\\s\\-&.,'()]+$",
                message = "Company name contains invalid characters")
        String company,

        @Size(max = 100, message = "Job title must not exceed 100 characters")
        @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9\\s\\-&.,'()/+]+$",
                message = "Job title contains invalid characters")
        String jobTitle,

        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio
) {
}
