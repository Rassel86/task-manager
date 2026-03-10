package com.viacheslav.taskmanager.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProjectUpdateRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Project name must not exceed 255 characters")
        String name,

        @Size(max = 2000, message = "Project description must not exceed 2000 characters")
        @NotBlank(message = "Description is required")
        String description
) {
}
