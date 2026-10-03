package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
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

    private final Booking booking;
    private final Vehicle vehicle;
    private String description;
    private Mechanic currentMechanic;
    private List<Mechanic> choosableMechanics;

    private ComboBox<Mechanic> mechanicComboBox;
    private TextArea descriptionField;

    public BookingDetails(Booking booking,
                          List<Mechanic> choosableMechanics,
                          Consumer<Booking> onSave,
                          Consumer<Booking> onDelete) {
        this.booking = booking;
        this.choosableMechanics = choosableMechanics;
        this.onSave = onSave;
        this.onDelete = onDelete;
        this.description = booking.getDescription();

        HBox topBox = new HBox(10,
                new Label(String.format("Date: %s", booking.getDate().toString())),
                new Label(String.format("ID: %d",booking.getId()))
        );

        this.vehicle = booking.getVehicle();
        //If there is no vehicle, the booking is faulty
        if(vehicle == null){
            this.setTop(topBox);
            this.setCenter(new Label("No vehicle connected to booking"));
            //TODO button to remove faulty booking
            return;
        }

        if(booking.getMechanic() != null) {
            this.currentMechanic = booking.getMechanic();
        }

        //TODO Should booking work if vehicleOwner is null?
        Customer vehicleOwner = vehicle.getCustomer();
        VehicleCard vehicleBox = new VehicleCard(vehicle, vehicleOwner);

        //Mechanics form
        mechanicComboBox = createMechanicComboBox();
        Label mechanicLabel = new Label("Mechanic");
        mechanicLabel.getStyleClass().add("form-field-label");

        VBox mechanicBox = new VBox(mechanicLabel, mechanicComboBox);
        mechanicBox.getStyleClass().add("form-field-container");

        //Description form
        descriptionField = new TextArea(description);
        Label descriptionLabel = new Label("Description of problem");
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

        BorderPane actionableButtons = new BorderPane(null, null, confirmingButtons, null, destructiveButtons);

        VBox detailsBox = new VBox(vehicleBox, mechanicBox, descriptionBox, actionableButtons);
        detailsBox.getStyleClass().add("details-container");

        this.setTop(topBox);
        this.setCenter(detailsBox);
        //this.setBottom(actionableButtons);
    }

    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList(choosableMechanics);

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

            onSave.accept(newBooking);
        });

        return saveBtn;
    }

    private Button createDeleteBtn() {
        Button deleteBtn = new Button("Delete");

        deleteBtn.setOnAction(e ->
                onDelete.accept(booking)

        );
        return deleteBtn;
    }
}
