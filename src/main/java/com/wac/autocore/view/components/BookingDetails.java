package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class BookingDetails extends BorderPane {
    Consumer<Booking> onSave;
    Consumer<Booking> onDelete;

    private Booking booking;
    private Vehicle vehicle;
    private String description;
    private Mechanic currentMechanic;
    private List<Mechanic> choosableMechanics;

    private ComboBox<Mechanic> mechanicComboBox;
    private TextArea descriptionField;

    public BookingDetails(Booking booking, List<Mechanic> choosableMechanics,
                          Consumer<Booking> onSave, Consumer<Booking> onDelete) {
        this.booking = booking;
        this.choosableMechanics = choosableMechanics;
        this.onSave = onSave;
        this.onDelete = onDelete;
        HBox topBox = new HBox(10,
                new Label(String.format(LanguageManager.getString("booking.date"), booking.getDate().toString())),
                new Label(String.format(LanguageManager.getString("booking.id"), booking.getId()))
        );

        this.vehicle = booking.getVehicle();
        if(vehicle == null){
            this.setTop(topBox);
            this.setCenter(new Label(LanguageManager.getString("booking.error.noVehicleConnected")));
            return;
        }


        if(booking.getMechanic() != null) {
            this.currentMechanic = booking.getMechanic();
        }

        this.description = booking.getDescription();

        //TODO Should booking work if vehicleOwner is null?
        Customer vehicleOwner = vehicle.getCustomer();
        //Customer vehicleOwner = garageSystem.getCustomer(vehicle.getCustomerId()).orElse(null);
        VehicleCard vehicleBox = new VehicleCard(vehicle, vehicleOwner);

        mechanicComboBox = createMechanicComboBox();
        HBox mechanicBox = new HBox(mechanicComboBox);

        descriptionField = new TextArea(description);
        HBox descriptionBox = new HBox(descriptionField);

        HBox actionableButtons = new HBox(20,
                createDeleteBtn(), createSaveBtn(), createWorkOrderBtn());

        this.setTop(topBox);
        this.setCenter(new VBox(vehicleBox, mechanicBox, descriptionBox));
        this.setBottom(actionableButtons);
    }

    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(choosableMechanics);

        ComboBox<Mechanic> comboBox =
                new ComboBox<>(mechanics);

        comboBox.setPromptText(LanguageManager.getString("booking.chooseMechanic"));
        comboBox.setConverter(ComboBoxLabels.mechanic());

        if(currentMechanic != null){
            comboBox.setValue(currentMechanic);
        }

        return comboBox;
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
            currentMechanic = mechanicComboBox.getValue();
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