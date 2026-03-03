package com.viacheslav.taskmanager.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserCreateRequest(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
