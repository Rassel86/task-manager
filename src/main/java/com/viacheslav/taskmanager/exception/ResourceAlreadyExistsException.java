package com.viacheslav.taskmanager.exception;

public class ResourceAlreadyExistsException extends BusinessLogicException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
