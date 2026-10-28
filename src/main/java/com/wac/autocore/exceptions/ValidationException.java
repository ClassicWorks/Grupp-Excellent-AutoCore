package com.wac.autocore.exceptions;

public abstract class ValidationException extends AutoCoreException {
    public ValidationException(String message) {
        super(message);
    }
}
