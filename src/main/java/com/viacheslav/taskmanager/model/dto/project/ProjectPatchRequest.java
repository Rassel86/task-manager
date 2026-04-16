package com.viacheslav.taskmanager.model.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectPatchRequest(

        @Size(max = 255 , message = "Project name must not exceed 255 characters")
        String name,

        @Size(max = 2000, message = "Project description must not exceed 2000 characters")
        String description
) {
}
