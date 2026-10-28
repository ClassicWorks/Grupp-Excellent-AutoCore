package com.wac.autocore.exceptions;

public class NotAuthenticatedException extends AutoCoreException {
    public NotAuthenticatedException() {
        super("You must be logged in to perform this operation.");
    }
}
