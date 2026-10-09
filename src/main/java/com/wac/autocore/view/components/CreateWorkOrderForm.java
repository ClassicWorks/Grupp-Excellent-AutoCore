package com.wac.autocore.view.components;

import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.ValueLabels;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateWorkOrderForm {
    private final GarageSystem garageSystem;
    private final Stage popupStage;

    public CreateWorkOrderForm(Stage popupStage) {
        garageSystem = new GarageSystem();
        this.popupStage = popupStage;
    }

    public Parent show(int bookingId) {
        return show(bookingId, new ArrayList<>());
    }

    public Parent show(int bookingId, List<Integer> preselectedServiceIds) {
        VBox root = new VBox();
        root.getStyleClass().add("form-container");


        Label heading = new Label(LanguageManager.getString("workorder.form.title"));
        heading.getStyleClass().add("form-title");

        Button cancelBtn = new Button(LanguageManager.getString("workorder.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");
        cancelBtn.setOnAction(e -> popupStage.close());

        //Check if required objects exist
        Optional<Booking> optionalBooking = garageSystem.getBooking(bookingId);
        if(!optionalBooking.isPresent()){
            Label errorLabel = new Label(LanguageManager.getString("workorder.form.error.bookingNotFound"));
            errorLabel.getStyleClass().add("error-label");

            root.getChildren().addAll(heading, errorLabel, cancelBtn);
            return root;
        }

        Booking booking = optionalBooking.get();
        Vehicle vehicle = booking.getVehicle();
        if(vehicle == null){
            Label errorLabel = new Label(LanguageManager.getString("workorder.form.error.vehicleNotFound"));
            errorLabel.getStyleClass().add("error-label");

            root.getChildren().addAll(heading, errorLabel, cancelBtn);
            return root;
        }

        BookingCard bookingCard = new BookingCard(booking, vehicle);
        bookingCard.getStyleClass().add("compact");

        Label mechanicLabel = new Label(LanguageManager.getString("booking.chooseMechanic"));
        mechanicLabel.getStyleClass().add("form-field-label");

        ComboBox<Mechanic> mechanicComboBox = createMechanicComboBox();

        VBox mechanicBox = new VBox(mechanicLabel, mechanicComboBox);
        mechanicBox.getStyleClass().add("form-field-container");

        Label serviceItemLabel = new Label(LanguageManager.getString("workorder.form.chooseServiceItems"));
        serviceItemLabel.getStyleClass().add("form-field-label");

        VBox serviceItemsCheckBoxes = new VBox();
        serviceItemsCheckBoxes.getStyleClass().add("check-box-container");

        List<CheckBox> serviceCheckBoxes = new ArrayList<>();
        for(ServiceItem service : garageSystem.getServiceItems()){
            CheckBox checkBox = new CheckBox(ValueLabels.serviceName(service.getName()));
            checkBox.setUserData(service.getId());
            checkBox.setSelected(preselectedServiceIds.contains(service.getId()));
            serviceCheckBoxes.add(checkBox);
            serviceItemsCheckBoxes.getChildren().add(checkBox);
        }

        VBox serviceItemsBox = new VBox(serviceItemLabel, serviceItemsCheckBoxes);
        serviceItemsBox.getStyleClass().add("form-field-container");

        Label formErrorLabel = new Label();
        formErrorLabel.getStyleClass().add("error-label");

        Button submitBtn = new Button(LanguageManager.getString("booking.createWorkOrder"));
        submitBtn.getStyleClass().add("confirm-btn");

        HBox actionableBtns = new HBox(submitBtn, cancelBtn);
        actionableBtns.getStyleClass().add("btn-container");

        submitBtn.setOnAction(e -> {
                    formErrorLabel.setText("");

                    Mechanic selectedMechanic = mechanicComboBox.getValue();

                    if (selectedMechanic == null) {
                        formErrorLabel.setText(LanguageManager.getString("workorder.form.error.noMechanic"));
                        return;
                    }

                    int[] selectedServiceItems = serviceCheckBoxes.stream()
                            .filter(CheckBox::isSelected)
                            .mapToInt(s -> (int) s.getUserData())
                            .toArray();

                    WorkOrder createdWorkOrder = garageSystem.createWorkOrder(
                            bookingId,
                            selectedMechanic.getId(),
                            selectedServiceItems);
                    //TODO dialog box if error occurs

                    if (createdWorkOrder == null) {
                        formErrorLabel.setText(LanguageManager.getString("workorder.form.error.failed"));
                        return;
                    }

                    popupStage.close();

                    AppDialog.showInformation(
                            LanguageManager.getString("workorder.form.dialog.workorderCreated.title"),
                            LanguageManager.getString("workorder.form.dialog.workorderCreated.header"),
                            String.format(LanguageManager.getString("workorder.form.dialog.workorderCreated.message"),
                                    createdWorkOrder.getId())

                    );
                }
        );

        root.getChildren().addAll(
                heading,
                bookingCard,
                mechanicBox,
                serviceItemsBox,
                formErrorLabel,
                actionableBtns
        );
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList();

        mechanics.addAll(garageSystem.getAvailableMechanics());

        ComboBox<Mechanic> comboBox =
                new ComboBox<>(mechanics);

        comboBox.setPromptText(LanguageManager.getString("booking.chooseMechanic"));
        comboBox.setConverter(ComboBoxLabels.mechanic());

        return comboBox;
    }
}