package com.wac.autocore.view.components;

import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.util.ValueLabels;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class WorkOrderCard extends VBox {

    public WorkOrderCard(WorkOrder workOrder, Consumer<WorkOrder> onCardClick) {
        Label titleLabel = new Label(String.format(
                LanguageManager.getString("workorder.card.title"), workOrder.getId()));
        titleLabel.getStyleClass().add("card-title");

        Label statusLabel = new Label(ValueLabels.workOrderStatus(workOrder.getStatus()));
        statusLabel.getStyleClass().add("status-label");

        HBox header = new HBox(10, titleLabel, statusLabel);

        Vehicle vehicle = workOrder.getBooking() != null ? workOrder.getBooking().getVehicle() : null;

        Label vehicleLabel = new Label(vehicle != null
                ? String.format("%s | %s %s", vehicle.getRegistrationNumber(), vehicle.getBrand(), vehicle.getModel())
                : LanguageManager.getString("workorders.error.vehicleNotFound"));

        Customer customer = vehicle != null ? vehicle.getCustomer() : null;

        Label customerLabel = new Label(customer != null
        ? customer.getName()
        : LanguageManager.getString("customer.none.connected"));

        Label priceLabel = new Label(String.format(
                LanguageManager.getString("invoice.details.amountFormat"), workOrder.getTotalPrice()));

        this.getChildren().addAll(header, vehicleLabel, customerLabel, priceLabel);
        this.getStyleClass().addAll("card", "clickable", "work-order-card");
        this.setMaxWidth(Double.MAX_VALUE);

        this.setOnMouseClicked(e -> {
            StylingUtil.setSelected(this, "work-order-card");
            onCardClick.accept(workOrder);
        });
    }
}