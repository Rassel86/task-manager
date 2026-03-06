package com.viacheslav.taskmanager.dto;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        ZonedDateTime createAt,
        ZonedDateTime updatedAt
) {
}
