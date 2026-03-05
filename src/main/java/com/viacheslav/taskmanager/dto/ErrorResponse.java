package com.viacheslav.taskmanager.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path
) {
}
