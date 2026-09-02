package com.viacheslav.taskmanager.model.dto;

import lombok.Builder;

@Builder
public record AvatarResponse(
        String avatarUrl,
        String avatarKey
) {
}
