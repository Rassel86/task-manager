package com.viacheslav.taskmanager.exception;

public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException() {
    }

    public BusinessLogicException(String message) {
        super(message);
    }
}
