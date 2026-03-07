package com.viacheslav.taskmanager.dto.project;

import lombok.Builder;

@Builder
public record ProjectUpdateRequest(
        String name,
        String description
) {
}
