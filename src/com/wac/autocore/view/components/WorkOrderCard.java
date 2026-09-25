package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WorkOrderCard extends VBox {
    GarageSystem garageSystem = new GarageSystem();
    public WorkOrderCard(WorkOrder workOrder, Booking booking, Vehicle vehicle, Mechanic mechanic) {
        Label label = new Label(String.format("Work Order ID: %d, Booking ID: %d ",
                workOrder.getId(), booking.getId()));

        //VehicleInfo
        ImageView vehicleIcon = new ImageView("resources/imgs/car-solid.png");
        vehicleIcon.setFitHeight(40);
        vehicleIcon.setFitWidth(40);
        Label registrationNumber = new Label(vehicle.getRegistrationNumber());
        Label brandModelYear = new Label(String.format("%s %s %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear()));
        VBox vehicleText = new VBox(registrationNumber, brandModelYear);
        HBox vehicleInfo = new HBox(vehicleIcon, vehicleText);

        //mechanic info
        Hyperlink mechanicLink = new MechanicHyperLink(mechanic);

        Text description = new Text(booking.getDescription());

        HBox statusInfo = new HBox();
        statusInfo.setAlignment(Pos.CENTER_RIGHT);
        if(workOrder.getStatus().equalsIgnoreCase("CREATED")) {
            setStyle(this.getStyle() + "-fx-border-color: gray;");
            getStyleClass().add("created");

            Label statusLabel = new Label("Incoming");
            statusLabel.getStyleClass().addAll("status-label", "incoming");

            Button actionBtn = new Button("Start work");
            actionBtn.setOnAction(e -> {
                garageSystem.startWorkOrder(workOrder.getId());
                ViewManager.getInstance().showWorkOrders();
            });
            actionBtn.getStyleClass().add("start-work-btn");
            statusInfo.getChildren().addAll(statusLabel, actionBtn);
        } else if (workOrder.getStatus().equalsIgnoreCase("IN_PROGRESS")) {
            setStyle(this.getStyle() + "-fx-border-color: yellow;");
            getStyleClass().add("in-progress");

            Label statusLabel = new Label("In Progress");
            statusLabel.getStyleClass().addAll("status-label", "in-progress");
            Button actionBtn = new Button("complete work");
            actionBtn.getStyleClass().add("in-progress-work-btn");
            actionBtn.setOnAction(e -> {
                garageSystem.completeWorkOrder(workOrder.getId());
                ViewManager.getInstance().showWorkOrders();
            });
            statusInfo.getChildren().addAll(statusLabel, actionBtn);
        } else if (workOrder.getStatus().equalsIgnoreCase("COMPLETED")) {
            setStyle(this.getStyle() + "-fx-border-color: green;");
            getStyleClass().add("completed");

            Label statusLabel = new Label("Completed");
            statusLabel.getStyleClass().addAll("status-label", "completed");
            statusInfo.getChildren().add(statusLabel);
        }else{
            Label statusLabel = new Label("Unknown");
            statusLabel.getStyleClass().addAll("status-label");
            statusInfo.getChildren().add(statusLabel);
        }

        this.getStyleClass().add("work-order-card");
        this.getStyleClass().add("card");

        this.getChildren().addAll(label, vehicleInfo, mechanicLink, description, statusInfo);
    }
}
