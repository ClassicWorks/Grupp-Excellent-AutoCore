package com.wac.autocore.data;

import com.wac.autocore.model.WorkOrder;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepo {
    WorkOrder save(WorkOrder wo);
    WorkOrder update(WorkOrder wo);
    List<WorkOrder> getAll();
    Optional<WorkOrder> get(int id);
    Optional<WorkOrder> getWithServiceItems(int id);
}
