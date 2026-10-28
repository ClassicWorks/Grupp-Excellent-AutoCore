package com.wac.autocore.exceptions;

public abstract class BusinessRuleException extends AutoCoreException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
