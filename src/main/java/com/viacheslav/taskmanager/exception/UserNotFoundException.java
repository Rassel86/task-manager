package com.viacheslav.taskmanager.exception;

public class UserNotFoundException extends BusinessLogicException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
