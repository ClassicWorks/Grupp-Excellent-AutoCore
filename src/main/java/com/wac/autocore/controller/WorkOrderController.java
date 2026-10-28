package com.wac.autocore.controller;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderType;
import com.wac.autocore.service.WorkOrderService;

public class WorkOrderController {
    private final WorkOrderService workOrderService;
    private final ErrorHandler errorHandler;

    public WorkOrderController(WorkOrderService workOrderService, ErrorHandler errorHandler) {
        this.workOrderService = workOrderService;
        this.errorHandler = errorHandler;
    }

    public Result<Void> confirm(int workOrderId) {
        return errorHandler.run(() -> workOrderService.confirmWorkOrder(workOrderId));
    }

    public Result<Void> start(int workOrderId) {
        return errorHandler.run(() -> workOrderService.startWorkOrder(workOrderId));
    }

    public Result<Void> complete(int workOrderId) {
        return errorHandler.run(() -> workOrderService.completeWorkOrder(workOrderId));
    }

    public Result<Void> cancel(int workOrderId) {
        return errorHandler.run(() -> workOrderService.cancelWorkOrder(workOrderId));
    }

    public Result<WorkOrder> updateType(int workOrderId, WorkOrderType type) {
        return errorHandler.call(() -> workOrderService.updateWorkOrderType(workOrderId, type));
    }

    public Result<WorkOrder> updateMechanic(int workOrderId, int mechanicId) {
        return errorHandler.call(() -> workOrderService.updateWorkOrderMechanic(workOrderId, mechanicId));
    }
}
