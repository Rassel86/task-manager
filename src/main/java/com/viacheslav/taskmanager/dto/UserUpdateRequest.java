package com.viacheslav.taskmanager.dto;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String username,
        String email
) {
}
