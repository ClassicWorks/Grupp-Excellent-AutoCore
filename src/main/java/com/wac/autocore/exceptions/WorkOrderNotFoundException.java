package com.wac.autocore.exceptions;

public class WorkOrderNotFoundException extends BusinessRuleException {
    public WorkOrderNotFoundException(String message) {
        super(message);
    }
    public WorkOrderNotFoundException(int id){
        super("Work order with ID " + id + " does not exist.");
    }
}
