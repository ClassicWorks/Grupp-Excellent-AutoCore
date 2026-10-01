package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class VehicleCard extends VBox{
    /**
     * Only information about Vehicle
     * @param vehicle
     */
    public VehicleCard(Vehicle vehicle) {
        HBox vehicleBox = createVehicleBox(vehicle);
        vehicleBox.getStyleClass().add("card-info-box");

        this.getChildren().add(vehicleBox);
        setStyle();
    }

    /**
     * Information about vehicle and customer
     * @param vehicle
     * @param customer
     */
    public VehicleCard(Vehicle vehicle, Customer customer) {
        this(vehicle);

        HBox customerInfoBox = new HBox(new CustomerHyperLink(customer));
        customerInfoBox.getStyleClass().add("card-info-box");

        this.getChildren().add(customerInfoBox);
    }

    /**
     * Information about vehicle and customer and a button for creating a booking
     * @param vehicle
     * @param customer
     * @param onCreateBooking
     */
    public VehicleCard(
            Vehicle vehicle,
            Customer customer,
            Consumer<Vehicle> onCreateBooking
    ) {
        this(vehicle, customer);

        HBox buttonBox = actionableButtons(onCreateBooking, vehicle);
        this.getChildren().add(buttonBox);
    }

    private static HBox actionableButtons(Consumer<Vehicle> onCreateBooking, Vehicle vehicle) {
        Button bookingBtn = new Button("Create booking");
        bookingBtn.getStyleClass().addAll("confirm-btn");

        bookingBtn.setOnAction(e -> onCreateBooking.accept(vehicle));

        HBox buttonBox = new HBox(bookingBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        return buttonBox;
    }

    private static HBox createVehicleBox(Vehicle vehicle) {
        ImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40, 40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %s, %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear())
        );
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);
        return vehicleBox;
    }

    private void setStyle() {
        this.setStyle("-fx-border-color: blue");
        this.getStyleClass().add("card");
        this.setMaxWidth(Double.MAX_VALUE);
    }
}
