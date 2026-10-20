package com.wac.autocore.model;

public enum WorkOrderStatus {
    DRAFT,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canEdit() {
        return this == DRAFT || this == CONFIRMED;
    }

    public boolean canBeCancelled() {
        return canTransitionTo(CANCELLED);
    }

    public boolean canTransitionTo(WorkOrderStatus target) {
        switch (this) {
            case DRAFT:
                return target == CONFIRMED || target == CANCELLED;
            case CONFIRMED:
                return target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS:
                return target == COMPLETED || target == CANCELLED;
            default:
                return false;
        }
    }
}
