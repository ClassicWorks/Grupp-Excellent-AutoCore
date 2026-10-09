package com.wac.autocore.model;

public enum WorkOrderStatus {
    DRAFT,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canEditServices () {
        return this == DRAFT || this == CONFIRMED;
    }

    public boolean canBeCancelled () {
        return this == DRAFT || this == CONFIRMED || this == IN_PROGRESS;
    }
}
