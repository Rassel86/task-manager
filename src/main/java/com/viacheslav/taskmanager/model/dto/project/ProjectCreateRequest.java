package com.viacheslav.taskmanager.model.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProjectCreateRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Project name must not exceed 255 characters")
        String name,

        @Size(max = 2000, message = "Project description must not exceed 2000 characters")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description,

        @NotNull(message = "Project owner is required")
        UUID ownerId
) {
}
