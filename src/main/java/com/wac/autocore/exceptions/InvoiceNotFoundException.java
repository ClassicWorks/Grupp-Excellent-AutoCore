package com.wac.autocore.exceptions;

public class InvoiceNotFoundException extends BusinessRuleException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
    public InvoiceNotFoundException(int id){
        super("Invoice with ID " + id + " does not exist.");
    }
}
