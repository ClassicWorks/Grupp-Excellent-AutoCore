package com.wac.autocore.view.components;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WorkOrderCard extends VBox {
    GarageSystem garageSystem = new GarageSystem();
    private WorkOrder workOrder;
    private Booking booking;
    private Vehicle vehicle;
    private Mechanic mechanic;

    public WorkOrderCard(WorkOrder workOrder, Booking booking, Vehicle vehicle, Mechanic mechanic) {
        this.workOrder = workOrder;
        this.booking = booking;
        this.vehicle = vehicle;
        this.mechanic = mechanic;

        Label idsLabel = new Label(String.format("Work Order ID: %d, Booking ID: %d ",
                workOrder.getId(), booking.getId()));
        Label statusLabel = createStatusLabel();
        HBox statusInfo = new HBox(idsLabel, statusLabel);

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

        this.getStyleClass().add("work-order-card");
        this.getStyleClass().add("card");
        this.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(statusInfo, vehicleInfo, mechanicLink, description);
    }

    public Label createStatusLabel() {
        Label statusLabel;
        switch (workOrder.getStatus().toUpperCase()) {
            case "CREATED":
                setStyle(this.getStyle() + "-fx-border-color: gray;");
                getStyleClass().add("created");

                statusLabel = new Label("Incoming");
                statusLabel.getStyleClass().add("incoming");
                break;

            case "IN_PROGRESS":
                setStyle(this.getStyle() + "-fx-border-color: yellow;");
                getStyleClass().add("in-progress");

                statusLabel = new Label("In Progress");
                statusLabel.getStyleClass().add("in-progress");
                break;

            case "COMPLETED":
                setStyle(this.getStyle() + "-fx-border-color: green;");
                getStyleClass().add("completed");

                statusLabel = new Label("Completed");
                statusLabel.getStyleClass().add("completed");
                break;

            default:
                statusLabel = new Label("Unknown");
                break;
        }

        statusLabel.getStyleClass().addAll("status-label");
        return statusLabel;
    }
}
