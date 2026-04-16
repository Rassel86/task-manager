package com.viacheslav.taskmanager.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermittedEndpoint {
    AUTH("/api/v1/auth/**"),
    PUBLIC("/api/v1/public/**");

    private final String pathPrefix;
}
