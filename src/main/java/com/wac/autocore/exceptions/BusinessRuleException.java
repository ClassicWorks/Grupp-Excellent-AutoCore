package com.wac.autocore.exceptions;

public abstract class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
