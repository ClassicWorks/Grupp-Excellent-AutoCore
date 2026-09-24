package com.wac.autocore.view.components;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class BookingCard extends HBox {
    public BookingCard(Booking booking, Vehicle vehicle, Mechanic mechanic){
        VBox vehicleCard = new VBox();
        vehicleCard.setStyle("-fx-border-color: blue");
        vehicleCard.getStyleClass().add("vehicle-card");

        //date
        Label date = new Label(booking.getDate().toString());
        VBox dateBox = new VBox(date);

        //Info about booked vehicle
        ImageView vehicleIcon = new ImageView("resources/imgs/car-solid.png");
        vehicleIcon.setFitHeight(40);
        vehicleIcon.setFitWidth(40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %2s, %d",
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info about mechanic
        ImageView mechanicIcon = new ImageView("resources/imgs/wrench-solid.png");
        mechanicIcon.setFitWidth(20);
        mechanicIcon.setFitHeight(20);
        HBox customerInfoBox = new HBox(mechanicIcon);

        if(mechanic != null){
            Hyperlink mechanicLink = new Hyperlink(mechanic.getName());
            mechanicLink.setOnAction(e -> System.out.printf("Calling ViewManager.getInstance().showMechanicsView(%d)\n", mechanic.getId()));
            customerInfoBox.getChildren().add(mechanicLink);
        } else {
            Label mechanicLabel = new Label("No mechanic assigned");
            customerInfoBox.getChildren().add(mechanicLabel);
        }

        //Actionable buttons
        Button createWorkOrderBtn = new Button("Create work order");
        createWorkOrderBtn.getStyleClass().add("create-btn");

        createWorkOrderBtn.setOnAction(e ->
                System.out.printf("Calling ViewManager.createWorkOrder(%d)\n",booking.getId())
        );
        HBox buttonBox = new HBox(createWorkOrderBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);

        ImageView editIcon = new ImageView("resources/imgs/pen-to-square-solid.png");
        editIcon.setFitHeight(30);
        editIcon.setFitWidth(30);
        ImageView deleteIcon = new ImageView("resources/imgs/trash-solid.png");
        deleteIcon.setFitHeight(30);
        deleteIcon.setFitWidth(30);

        Button editBookingBtn = new Button("redigera", editIcon);
        editBookingBtn.getStyleClass().addAll("edit-card-btn");
        editBookingBtn.setOnAction(e ->
                System.out.printf("Calling ViewManager.editBooking(%d)\n",booking.getId())
        );

        Button deleteBookingBtn = new Button("Radera", deleteIcon);
        deleteBookingBtn.getStyleClass().addAll("delete-card-btn");
        deleteBookingBtn.setOnAction(e ->
                System.out.printf("Calling ViewManager.deleteBooking(%d)\n",booking.getId())
        );
        VBox actionableBox = new VBox(editBookingBtn, deleteBookingBtn);

        vehicleCard.getChildren().add(dateBox);
        vehicleCard.getChildren().add(vehicleBox);
        vehicleCard.getChildren().add(customerInfoBox);
        vehicleCard.getChildren().add(buttonBox);
        this.getChildren().addAll(vehicleCard, actionableBox);
    }
}
