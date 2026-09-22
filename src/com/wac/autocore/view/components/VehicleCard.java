package com.wac.autocore.view.components;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class VehicleCard {
    public static Node getCard(Vehicle vehicle){
        VBox vehicleCard = new VBox();
        vehicleCard.setStyle("-fx-border-color: blue");
        vehicleCard.getStyleClass().add("vehicle-card");

        //Info om bilen
        ImageView vehicleIcon = new ImageView(new Image("resources/imgs/car-solid.png"));
        vehicleIcon.setFitHeight(40);
        vehicleIcon.setFitWidth(40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %2s, %d",
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info om kund
        ImageView customerIcon = new ImageView("resources/imgs/user-solid.png");
        customerIcon.setFitWidth(20);
        customerIcon.setFitHeight(20);
        //TODO if no customer connected what should happen? Is Customer always needed? Remove is no customer
        Hyperlink customerLink = new Hyperlink(
                Database.getCustomers().stream()
                        .filter(c -> c.getId() == vehicle.getCustomerId())
                        .findFirst()
                        .map(Customer::getName)
                        .orElse("No customer connected"));
        customerLink.setOnAction(e -> System.out.printf("Calling ViewManager.showCustomer(%d)\n",vehicle.getCustomerId()));
        HBox customerInfoBox = new HBox(customerIcon, customerLink);

        //Actionable buttons
        Button bookingBtn = new Button("Boka");
        bookingBtn.getStyleClass().add("create-btn");

        bookingBtn.setOnAction(e -> System.out.printf("Calling ViewManager.createBooking(%d)\n",vehicle.getId()));
        HBox buttonBox = new HBox(bookingBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);

        ImageView editIcon = new ImageView("resources/imgs/pen-to-square-solid.png");
        editIcon.setFitHeight(30);
        editIcon.setFitWidth(30);
        ImageView deleteIcon = new ImageView("resources/imgs/trash-solid.png");
        deleteIcon.setFitHeight(30);
        deleteIcon.setFitWidth(30);
        Button editVehicleBtn = new Button("redigera", editIcon);
        Button deleteVehicleBtn = new Button("Radera", deleteIcon);
        VBox actionableBox = new VBox(editVehicleBtn, deleteVehicleBtn);



        vehicleCard.getChildren().add(vehicleBox);
        vehicleCard.getChildren().add(customerInfoBox);
        vehicleCard.getChildren().add(buttonBox);
        HBox cardWBtns = new HBox(vehicleCard, actionableBox);
        return cardWBtns;
    }
}
