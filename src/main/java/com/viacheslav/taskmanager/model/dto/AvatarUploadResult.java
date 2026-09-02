package com.viacheslav.taskmanager.model.dto;

import lombok.Builder;

@Builder
public record AvatarUploadResult(
        String avatarKey,
        String avatarUrl
) {
}
