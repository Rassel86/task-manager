package com.viacheslav.taskmanager.dto;

import lombok.Builder;

@Builder
public record ProjectPatchRequest(
        String name,
        String description
) {
}
