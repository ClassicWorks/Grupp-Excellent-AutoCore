package com.wac.autocore.exceptions;

public class VehicleNotFoundException extends BusinessRuleException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
    public VehicleNotFoundException(int id){
        super("Vehicle with ID " + id + " does not exist.");
    }
}
