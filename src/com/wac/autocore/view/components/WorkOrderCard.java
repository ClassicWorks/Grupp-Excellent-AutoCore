package com.wac.autocore.view.components;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class WorkOrderCard extends HBox {
    public WorkOrderCard(WorkOrder workOrder, Booking booking, Vehicle vehicle, Mechanic mechanic) {
        Label label = new Label(String.format("Work Order ID: %d, Booking ID: %d "
                ,workOrder.getId(), booking.getId()));

        this.getChildren().add(label);
    }
}
