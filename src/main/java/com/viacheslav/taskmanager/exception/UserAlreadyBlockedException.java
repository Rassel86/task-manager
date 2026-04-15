package com.viacheslav.taskmanager.exception;

public class UserAlreadyBlockedException extends RuntimeException {

    public UserAlreadyBlockedException(String message) {
        super(message);
    }
}
