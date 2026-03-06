package com.viacheslav.taskmanager.dto;

import lombok.Builder;

@Builder
public record ProjectUpdateRequest(
        String name,
        String description
) {
}
