package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VehicleCard extends VBox{
    public VehicleCard(Vehicle vehicle, Customer customer){
        //Info om bilen
        ImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40, 40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %2s, %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear())
        );
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info om kund
        HBox customerInfoBox = new HBox(new CustomerHyperLink(customer));

        //Actionable buttons
        Button bookingBtn = new Button("Boka");
        bookingBtn.getStyleClass().addAll("create-btn");

        bookingBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateBooking(vehicle.getId())
        );
        HBox buttonBox = new HBox(bookingBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);


        this.setStyle("-fx-border-color: blue");
        this.getStyleClass().add("vehicle-card");

        this.getChildren().add(vehicleBox);
        this.getChildren().add(customerInfoBox);
        this.getChildren().add(buttonBox);
        this.setMaxWidth(Double.MAX_VALUE);
    }

    public VehicleCard(Vehicle vehicle) {
        ImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40,40);

        Label registrationNumber =
                new Label(vehicle.getRegistrationNumber());

        Label vehicleInfo = new Label(
                String.format("%s - %s, %d",
                        vehicle.getBrand(),
                        vehicle.getModel(),
                        vehicle.getYear()
                )
        );

        VBox information = new VBox(
                registrationNumber,
                vehicleInfo
        );

        HBox vehicleInfoBox = new HBox(vehicleIcon,
                information);

        this.getChildren().addAll(
                vehicleInfoBox
        );

        this.setStyle("-fx-border-color: blue");
        this.getStyleClass().add("card");
        this.setMaxWidth(Double.MAX_VALUE);
    }
}
