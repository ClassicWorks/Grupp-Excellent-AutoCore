package com.wac.autocore.view.components;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;


public class BookingCard extends VBox {
    public BookingCard(Booking booking, Vehicle vehicle){
        //date
        VBox dateBox = getDateBox(booking);
        dateBox.getStyleClass().add("card-info-box");

        //Info about booked vehicle
        VBox vehicleInfoBox = getVehicleInfoBox(vehicle);
        vehicleInfoBox.getStyleClass().add("card-info-box");

        IconImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40,40);
        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        this.getStyleClass().add("card");

        this.getChildren().add(dateBox);
        this.getChildren().add(vehicleBox);
    }

    public BookingCard(Booking booking, Vehicle vehicle, Consumer<Booking> onCreateWorkOrder){
        this(booking, vehicle);
        //Actionable buttons
        Button createWorkOrderBtn = getCreateWorkOrderBtn(onCreateWorkOrder, booking);

        HBox buttonBox = new HBox(createWorkOrderBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getStyleClass().addAll("card-info-box");

        this.getChildren().add(buttonBox);
    }

    private static Button getCreateWorkOrderBtn(Consumer<Booking> onCreateWorkOrder, Booking booking) {
        Button createWorkOrderBtn = new Button(LanguageManager.getString("booking.createWorkOrder"));
        createWorkOrderBtn.getStyleClass().addAll("confirm-btn", "card-action-btn");

        createWorkOrderBtn.setOnAction(e ->
                onCreateWorkOrder.accept(booking)
        );
        return createWorkOrderBtn;
    }

    private static VBox getVehicleInfoBox(Vehicle vehicle) {
        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format(
                "%s - %2s, %d",
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear()));
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);
        return vehicleInfoBox;
    }

    private static VBox getDateBox(Booking booking) {
        Label date = new Label(booking.getDate().toString());
        VBox dateBox = new VBox(date);
        return dateBox;
    }
}