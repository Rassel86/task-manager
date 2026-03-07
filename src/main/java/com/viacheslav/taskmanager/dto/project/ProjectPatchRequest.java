package com.viacheslav.taskmanager.dto.project;

import lombok.Builder;

@Builder
public record ProjectPatchRequest(
        String name,
        String description
) {
}
