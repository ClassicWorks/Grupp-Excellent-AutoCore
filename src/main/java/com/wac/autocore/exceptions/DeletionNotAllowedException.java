package com.wac.autocore.exceptions;

public class DeletionNotAllowedException extends BusinessRuleException {
    public DeletionNotAllowedException(String message) {
        super(message);
    }
}
