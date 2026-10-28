package com.wac.autocore.exceptions;

public class PermissionDeniedException extends AutoCoreException {
    public PermissionDeniedException() {
        super("You do not have permission to perform this operation.");
    }
}
