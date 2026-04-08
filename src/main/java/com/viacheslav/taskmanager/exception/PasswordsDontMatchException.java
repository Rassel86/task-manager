package com.viacheslav.taskmanager.exception;

public class PasswordsDontMatchException extends BusinessLogicException {

    public PasswordsDontMatchException(String message) {
        super(message);
    }

}
