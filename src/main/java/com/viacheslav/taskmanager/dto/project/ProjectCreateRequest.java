package com.viacheslav.taskmanager.dto.project;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProjectCreateRequest(
        String name,
        String description,
        UUID ownerId
) {
}
