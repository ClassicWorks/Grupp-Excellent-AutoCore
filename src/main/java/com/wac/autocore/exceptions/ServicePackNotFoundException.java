package com.wac.autocore.exceptions;

public class ServicePackNotFoundException extends BusinessRuleException {
    public ServicePackNotFoundException(String message) {
        super(message);
    }
    public ServicePackNotFoundException(int id){
        super("Service pack with ID " + id + " does not exist.");
    }
}
