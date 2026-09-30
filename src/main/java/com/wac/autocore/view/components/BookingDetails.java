package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BookingDetails extends BorderPane {
    private GarageSystem garageSystem = new GarageSystem();

    private Booking booking;
    private Vehicle vehicle;
    private Mechanic currentMechanic;
    private String description;

    private ComboBox<Mechanic> mechanicComboBox;
    private TextArea descriptionField;

    public BookingDetails(Booking booking) {
        this.booking = booking;
        HBox topBox = new HBox(10,
                new Label(String.format("Date: %s", booking.getDate().toString())),
                new Label(String.format("ID: %d",booking.getId()))
        );

        this.vehicle = booking.getVehicle();
        if(vehicle == null){
            this.setTop(topBox);
            this.setCenter(new Label("No vehicle connected to booking"));
            return;
        }


        if(booking.getMechanic() != null) {
            this.currentMechanic = booking.getMechanic();
        }

        this.description = booking.getDescription();

        //TODO Should booking work if vehicleOwner is null?
        Customer vehicleOwner = vehicle.getCustomer();
        //Customer vehicleOwner = garageSystem.getCustomer(vehicle.getCustomerId()).orElse(null);
        HBox vehicleBox = new VehicleCard(vehicle, vehicleOwner);

        mechanicComboBox = createMechanicComboBox(currentMechanic);
        HBox mechanicBox = new HBox(mechanicComboBox);

        descriptionField = new TextArea(description);
        HBox descriptionBox = new HBox(descriptionField);

        HBox actionableButtons = new HBox(20,
                createDeleteBtn(), createSaveBtn(), createWorkOrderBtn());

        this.setTop(topBox);
        this.setCenter(new VBox(vehicleBox, mechanicBox, descriptionBox));
        this.setBottom(actionableButtons);
    }

    private ComboBox<Mechanic> createMechanicComboBox(Mechanic currentMechanic) {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(garageSystem.getMechanics());

        ComboBox<Mechanic> comboBox =
                new ComboBox<>(mechanics);

        comboBox.setPromptText("Choose mechanic");

        if(currentMechanic != null){
            comboBox.setValue(currentMechanic);
        }

        return comboBox;
    }

    private Button createWorkOrderBtn() {
        Button bookVehicleBtn = new Button("Create Workorder");
        bookVehicleBtn.setOnAction(e ->
                ViewManager.getInstance().showCreateWorkOrderPopup(booking.getId())
        );
        return bookVehicleBtn;
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button("Save changes");

        //TODO check if everything is added
        //TODO savefunction
        saveBtn.setOnAction(e ->{
            currentMechanic = mechanicComboBox.getValue();
            description = descriptionField.getText();
            Booking newBooking = new Booking(vehicle, booking.getDate(), description);
            //newBooking.setMechanic(currentMechanic.getId());

            System.out.printf("Should call ViewManager.getInstance.saveBooking(%d, %s)", booking.getId(), newBooking);
        });

        return saveBtn;
    }

    private Button createDeleteBtn() {
        Button deleteBtn = new Button("Delete");

        deleteBtn.setOnAction(e ->
                System.out.printf("Should call ViewManager.getInstance.confirmDelete(vehicle, garagesystem.deleteBooking(%d)",
                        booking.getId())
        );
        return deleteBtn;
    }
}
