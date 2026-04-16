package com.viacheslav.taskmanager.exception;

public class PasswordSameAsOldException extends RuntimeException {

    public PasswordSameAsOldException(String message) {
        super(message);
    }
}
