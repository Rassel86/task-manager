package com.viacheslav.taskmanager.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.UUID;

@JsonInclude
@Builder
public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
