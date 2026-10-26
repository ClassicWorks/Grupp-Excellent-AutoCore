package com.wac.autocore.exceptions;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("You must be logged in to perform this operation.");
    }
}
