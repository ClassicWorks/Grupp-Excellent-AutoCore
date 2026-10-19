package com.wac.autocore.exceptions;

public class PaymentNotFoundException extends BusinessRuleException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
    public PaymentNotFoundException(int id){
        super("Payment with ID " + id + " does not exist.");
    }
}
