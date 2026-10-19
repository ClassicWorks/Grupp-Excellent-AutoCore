package com.wac.autocore.exceptions;

public abstract class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
