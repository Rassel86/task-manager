package com.viacheslav.taskmanager.model.enums;

import lombok.Getter;

@Getter
public enum TaskPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("Hight"),
    CRITICAL("Critical");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

}
