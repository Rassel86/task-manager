package com.viacheslav.taskmanager.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String displayName,
        String contactEmail,
        String avatarUrl,
        String role,
        String bio,
        String phoneNumber,
        String createdAt,
        String updatedAt,
        boolean enabled
) {
}
