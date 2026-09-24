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
        //date
        Label date = new Label(booking.getDate().toString());
        VBox dateBox = new VBox(date);

        //Info about booked vehicle
        ImageView vehicleIcon = new IconImageView("resources/imgs/car-solid.png", 40,40);
        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format(
                "%s - %2s, %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear()));
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info about mechanic
        ImageView mechanicIcon = new IconImageView("resources/imgs/wrench-solid.png", 20,20);
        HBox customerInfoBox = new HBox(mechanicIcon);

        if(mechanic != null){
            Hyperlink mechanicLink = new Hyperlink(mechanic.getName());
            mechanicLink.setOnAction(e ->
                    System.out.printf(
                        "Calling ViewManager.getInstance().showMechanicsView(%d)\n",
                        mechanic.getId())
            );
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

        ImageView editIcon = new IconImageView("resources/imgs/pen-to-square-solid.png", 30,30);
        ImageView deleteIcon = new IconImageView("resources/imgs/trash-solid.png", 30,30);

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

        VBox bookingCard = new VBox();
        bookingCard.setStyle("-fx-border-color: blue");
        bookingCard.getStyleClass().add("vehicle-card");

        bookingCard.getChildren().add(dateBox);
        bookingCard.getChildren().add(vehicleBox);
        bookingCard.getChildren().add(customerInfoBox);
        bookingCard.getChildren().add(buttonBox);
        this.getChildren().addAll(bookingCard, actionableBox);
    }
}
