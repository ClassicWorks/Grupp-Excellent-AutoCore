package com.wac.autocore.exceptions;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
        super("You do not have permission to perform this operation.");
    }
}
