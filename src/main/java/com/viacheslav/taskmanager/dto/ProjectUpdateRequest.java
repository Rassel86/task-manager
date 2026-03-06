package com.viacheslav.taskmanager.dto;

public record ProjectUpdateRequest(
        String name,
        String description
) {
}
