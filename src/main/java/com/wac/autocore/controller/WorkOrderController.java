package com.wac.autocore.controller;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderStatus;
import com.wac.autocore.model.WorkOrderType;
import com.wac.autocore.service.WorkOrderService;

import java.util.List;
import java.util.Optional;

public class WorkOrderController {
    private final WorkOrderService workOrderService;
    private final ErrorHandler errorHandler;

    public WorkOrderController(WorkOrderService workOrderService, ErrorHandler errorHandler) {
        this.workOrderService = workOrderService;
        this.errorHandler = errorHandler;
    }

    public Result<List<WorkOrder>> getWorkOrdersWithItemsByStatus(WorkOrderStatus status) {
        return errorHandler.call(() -> workOrderService.getWorkOrdersWithItemsByStatus(status));
    }

    public Result<List<WorkOrder>> getWorkOrdersWithItems() {
        return errorHandler.call(workOrderService::getWorkOrdersWithItems);
    }

    public Result<Optional<WorkOrder>> getWorkOrderWithItems(int workOrderId) {
        return errorHandler.call(() -> workOrderService.getWorkOrderWithItems(workOrderId));
    }

    public Result<WorkOrder> create(int bookingId, Integer mechanicId, WorkOrderType workOrderType, int... serviceItemIds) {
        return errorHandler.call(() ->
                workOrderService.createWorkOrder(bookingId, mechanicId, workOrderType, serviceItemIds));
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
