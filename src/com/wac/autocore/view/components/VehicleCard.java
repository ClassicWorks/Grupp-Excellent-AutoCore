package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VehicleCard extends HBox{
    public VehicleCard(Vehicle vehicle, Customer customer){
        //Info om bilen
        ImageView vehicleIcon = new IconImageView("resources/imgs/car-solid.png", 40, 40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %2s, %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear())
        );
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info om kund
        ImageView customerIcon = new IconImageView("resources/imgs/user-solid.png", 20, 20);

        HBox customerInfoBox = new HBox(customerIcon);
        if(vehicle.getCustomerId() <= 0 || customer == null) {
            Label noCustomer = new Label("No customer connected");
            customerInfoBox.getChildren().add(noCustomer);
        }
        else {
            Hyperlink customerLink = new Hyperlink(customer.getName());
            customerLink.setOnAction(e -> System.out.printf("Calling ViewManager.showCustomer(%d)\n", vehicle.getCustomerId()));
            customerInfoBox.getChildren().add(customerLink);
        }

        //Actionable buttons
        Button bookingBtn = new Button("Boka");
        bookingBtn.getStyleClass().addAll("create-btn");

        bookingBtn.setOnAction(e -> ViewManager.getInstance().showCreateBooking(vehicle.getId()));
        HBox buttonBox = new HBox(bookingBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);

        ImageView editIcon = new IconImageView("resources/imgs/pen-to-square-solid.png", 30, 30);
        ImageView deleteIcon = new IconImageView("resources/imgs/trash-solid.png", 30, 30);
        Button editVehicleBtn = new Button("Edit", editIcon);
        Button deleteVehicleBtn = new Button("Delete", deleteIcon);
        deleteVehicleBtn.getStyleClass().addAll("delete-card-btn");
        editVehicleBtn.getStyleClass().addAll("edit-card-btn");

        VBox actionableBox = new VBox(editVehicleBtn, deleteVehicleBtn);

        VBox vehicleCard = new VBox();
        vehicleCard.setStyle("-fx-border-color: blue");
        vehicleCard.getStyleClass().add("vehicle-card");

        vehicleCard.getChildren().add(vehicleBox);
        vehicleCard.getChildren().add(customerInfoBox);
        vehicleCard.getChildren().add(buttonBox);
        this.getChildren().addAll(vehicleCard, actionableBox);
    }

    public VehicleCard(Vehicle vehicle) {
        ImageView vehicleIcon = new IconImageView("resources/imgs/car-solid.png", 40,40);

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

        this.getChildren().addAll(
                vehicleIcon,
                information
        );

        this.setStyle("-fx-border-color: blue");
        this.getStyleClass().add("vehicle-card");
    }
}
