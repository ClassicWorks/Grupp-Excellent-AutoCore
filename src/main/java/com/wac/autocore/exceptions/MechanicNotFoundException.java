package com.wac.autocore.exceptions;

public class MechanicNotFoundException extends BusinessRuleException {
    public MechanicNotFoundException(String message) {
        super(message);
    }
    public MechanicNotFoundException(int id){
        super("Mechanic with ID " + id + " does not exist.");
    }
}
