package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class BookingDetails extends BorderPane {
    Consumer<Booking> onSave;
    Consumer<Booking> onDelete;

    private final Booking booking;
    private final Vehicle vehicle;
    private String description;

    private ComboBox<Mechanic> mechanicComboBox;
    private TextArea descriptionField;

    public BookingDetails(Booking booking,
                          Consumer<Booking> onSave,
                          Consumer<Booking> onDelete) {
        this.booking = booking;
        this.onSave = onSave;
        this.onDelete = onDelete;
        this.description = booking.getDescription();

        HBox topBox = new HBox(10,
                new Label(String.format(LanguageManager.getString("booking.date"), booking.getDate().toString())),
                new Label(String.format(LanguageManager.getString("booking.id"), booking.getId()))
        );

        this.vehicle = booking.getVehicle();
        //If there is no vehicle, the booking is faulty
        if(vehicle == null){
            this.setTop(topBox);
            this.setCenter(new Label(LanguageManager.getString("booking.error.noVehicleConnected")));
            //TODO button to remove faulty booking
            return;
        }

        //TODO Should booking work if vehicleOwner is null?
        Customer vehicleOwner = vehicle.getCustomer();
        VehicleCard vehicleBox = new VehicleCard(vehicle, vehicleOwner);

        //Description form
        descriptionField = new TextArea(description);
        Label descriptionLabel = new Label(LanguageManager.getString("booking.form.description"));
        descriptionLabel.getStyleClass().add("form-field-label");

        VBox descriptionBox = new VBox(descriptionLabel, descriptionField);
        descriptionBox.getStyleClass().add("form-field-container");

        Button deleteBtn = createDeleteBtn();
        deleteBtn.getStyleClass().add("destroy-btn");

        Button saveBtn = createSaveBtn();
        saveBtn.getStyleClass().add("confirm-btn");

        Button workOrderBtn = createWorkOrderBtn();
        workOrderBtn.getStyleClass().add("confirm-btn");

        HBox confirmingButtons = new HBox(saveBtn, workOrderBtn);
        confirmingButtons.getStyleClass().add("btn-container");
        HBox destructiveButtons = new HBox(deleteBtn);
        destructiveButtons.getStyleClass().add("btn-container");

        BorderPane actionableButtons = new BorderPane(null, null, destructiveButtons, null, confirmingButtons);

        VBox detailsBox = new VBox(
                vehicleBox,
                descriptionBox);
        detailsBox.getStyleClass().add("details-container");

        this.setTop(topBox);
        this.setCenter(detailsBox);
        this.setBottom(actionableButtons);

    }

    private Button createWorkOrderBtn() {
        Button bookVehicleBtn = new Button(LanguageManager.getString("booking.createWorkOrder"));
        bookVehicleBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateWorkOrderPopup(booking.getId())
        );
        return bookVehicleBtn;
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("booking.save"));

        //TODO check if everything is added
        //TODO savefunction
        saveBtn.setOnAction(e ->{
            //currentMechanic = mechanicComboBox.getValue();
            description = descriptionField.getText();
            Booking newBooking = new Booking(vehicle, booking.getDate(), description);
            //newBooking.setMechanic(currentMechanic.getId());

            onSave.accept(newBooking);
        });

        return saveBtn;
    }

    private Button createDeleteBtn() {
        Button deleteBtn = new Button(LanguageManager.getString("booking.delete"));

        deleteBtn.setOnAction(e ->
                onDelete.accept(booking)

        );
        return deleteBtn;
    }
}