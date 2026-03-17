package com.viacheslav.taskmanager.entity.enums;

public enum UserRole {
    ADMIN,
    MODERATOR,
    USER,
    GUEST;

    public String getAuthority() {
        return "ROLE_" + this;
    }
}
