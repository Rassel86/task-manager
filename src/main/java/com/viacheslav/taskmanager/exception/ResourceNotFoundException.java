package com.viacheslav.taskmanager.exception;

public class ResourceNotFoundException extends BusinessLogicException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
