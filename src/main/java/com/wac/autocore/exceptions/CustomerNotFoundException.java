package com.wac.autocore.exceptions;

public class CustomerNotFoundException extends BusinessRuleException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
    public CustomerNotFoundException(int id){
        super("Customer with ID " + id + " does not exist.");
    }
}
