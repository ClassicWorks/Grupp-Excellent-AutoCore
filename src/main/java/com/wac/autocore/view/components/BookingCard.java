package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class BookingCard extends VBox {
    public BookingCard(Booking booking, Vehicle vehicle, Mechanic mechanic){
        //date
        VBox dateBox = getDateBox(booking);
        dateBox.getStyleClass().add("card-info-box");

        //Info about booked vehicle
        VBox vehicleInfoBox = getVehicleInfoBox(vehicle);
        vehicleInfoBox.getStyleClass().add("card-info-box");

        IconImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40,40);
        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info about mechanic
        /*Hyperlink mechanicLink = new MechanicHyperLink(mechanic);
        mechanicLink.getStyleClass().add("card-info-box");*/

        //Actionable buttons
        Button createWorkOrderBtn = getCreateWorkOrderBtn(booking);

        HBox buttonBox = new HBox(createWorkOrderBtn);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getStyleClass().addAll("card-info-box");

        VBox bookingCard = new VBox();
        this.getStyleClass().add("card");

        this.getChildren().add(dateBox);
        this.getChildren().add(vehicleBox);
        //this.getChildren().add(mechanicLink);
        this.getChildren().add(buttonBox);
        this.getChildren().addAll(bookingCard);
    }

    private static Button getCreateWorkOrderBtn(Booking booking) {
        Button createWorkOrderBtn = new Button(LanguageManager.getString("booking.createWorkOrder"));
        createWorkOrderBtn.getStyleClass().addAll("confirm-btn", "card-action-btn");

        createWorkOrderBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateWorkOrderPopup(booking.getId())
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