package com.wac.autocore.exceptions;

public class ServiceItemNotFoundException extends BusinessRuleException {
    public ServiceItemNotFoundException(String message) {
        super(message);
    }
    public ServiceItemNotFoundException(int id){
        super("Service item with ID " + id + " does not exist.");
    }
}
