package com.viacheslav.taskmanager.dto.user;

import com.viacheslav.taskmanager.entity.enums.UserRole;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record UserFilterRequest(
        Integer size,
        Integer page,
        String sortField,
        String sortDirection,
        String email,
        String username,
        String firstName,
        String lastName,
        UserRole role,
        Boolean enabled,
        ZonedDateTime createdAt,
        ZonedDateTime lessCreatedAt,
        ZonedDateTime greaterCreatedAt

) {
    public UserFilterRequest {
        if (page == null || page < 0) page = 0;
        if (page > 100) page = 100;
        if (size == null || size < 0) size = 10;
        if (size > 100) size = 100;
        if (sortField == null) sortField = "createdAt";
        if (sortDirection == null) sortDirection = "asc";
    }
}
