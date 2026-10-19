package com.wac.autocore.data;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderStatus;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepo {
    WorkOrder save(WorkOrder wo);
    WorkOrder update(WorkOrder wo);
    List<WorkOrder> getAll();
    List<WorkOrder> getAllWithItems();
    Optional<WorkOrder> get(int id);
    List<WorkOrder> getCompletedWorkOrdersWithoutInvoices();
    Optional<WorkOrder> getWithItems(int id);
    boolean mechanicHasUncompletedWorkOrders(int mechanicId);
    List<WorkOrder> getWorkOrdersByMechanic(int mechanicId);
    List<WorkOrder> getWithStatus(WorkOrderStatus status);
}
