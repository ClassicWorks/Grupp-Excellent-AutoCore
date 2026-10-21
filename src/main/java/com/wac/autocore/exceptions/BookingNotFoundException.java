package com.wac.autocore.exceptions;

public class BookingNotFoundException extends BusinessRuleException {
    public BookingNotFoundException(String message) {
        super(message);
    }
    public BookingNotFoundException(int id) {
        super("Booking with ID " + id + " does not exist.");
    }
}
