package com.viacheslav.taskmanager.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
