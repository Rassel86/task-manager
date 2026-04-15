package com.viacheslav.taskmanager.model.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    TO_DO("To do"),
    IN_PROGRESS("In progress"),
    COMPLETED("Completed"),
    CANCELLED("Canceled");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

}
