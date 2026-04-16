package com.viacheslav.taskmanager.model.enums;

public enum UserRole {
    ADMIN,
    MODERATOR,
    USER,
    GUEST;

    public String getAuthority() {
        return "ROLE_" + this;
    }
}
