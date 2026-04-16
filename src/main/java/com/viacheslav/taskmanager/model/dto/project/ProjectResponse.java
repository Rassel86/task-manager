package com.viacheslav.taskmanager.model.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
        ZonedDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
        ZonedDateTime updatedAt
) {
}
