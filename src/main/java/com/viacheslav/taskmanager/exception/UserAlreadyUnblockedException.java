package com.viacheslav.taskmanager.exception;

public class UserAlreadyUnblockedException extends RuntimeException {

    public UserAlreadyUnblockedException(String message) {
        super(message);
    }
}
