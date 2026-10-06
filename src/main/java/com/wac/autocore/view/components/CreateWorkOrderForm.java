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

        Mechanic mechanic = booking.getMechanic();

        VBox bookingCard = createBookingCard(booking, vehicle, mechanic);

        Label mechanicLabel = new Label(LanguageManager.getString("booking.chooseMechanic"));
        mechanicLabel.getStyleClass().add("form-field-label");

        ComboBox<Mechanic> mechanicComboBox = createMechanicComboBox();
        if(mechanic != null){
            mechanicComboBox.setValue(mechanic);
        }

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
            serviceCheckBoxes.add(checkBox);
            serviceItemsCheckBoxes.getChildren().add(checkBox);
        }

        VBox serviceItemsBox = new VBox(serviceItemLabel, serviceItemsCheckBoxes);
        serviceItemsBox.getStyleClass().add("form-field-container");

        Button submitBtn = new Button(LanguageManager.getString("booking.createWorkOrder"));
        submitBtn.getStyleClass().add("confirm-btn");

        HBox actionableBtns = new HBox(submitBtn, cancelBtn);
        actionableBtns.getStyleClass().add("btn-container");

        submitBtn.setOnAction(e -> {
                    Mechanic selectedMechanic = mechanicComboBox.getValue();

                    int[] selectedServiceItems = serviceCheckBoxes.stream()
                            .filter(CheckBox::isSelected)
                            .mapToInt(s -> (int) s.getUserData())
                            .toArray();

                    WorkOrder createdWorkOrder = garageSystem.createWorkOrder(
                            bookingId,
                            selectedMechanic.getId(),
                            selectedServiceItems);
                    //TODO dialog box for confirmation
                    popupStage.close();
                }
        );

        root.getChildren().addAll(
                heading,
                bookingCard,
                mechanicBox,
                serviceItemsBox,
                actionableBtns
        );
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }


    //TODO move to BookingCard
    private VBox createBookingCard(Booking booking, Vehicle vehicle, Mechanic mechanic){
        //date
        Label date = new Label(booking.getDate().toString());
        VBox dateBox = new VBox(date);

        //Info about booked vehicle
        IconImageView vehicleIcon = new IconImageView("/imgs/car-solid.png", 40, 40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %2s, %d",
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleBox = new HBox(vehicleIcon, vehicleInfoBox);

        //Info about mechanic
        Hyperlink mechanicLink = new MechanicHyperLink(mechanic);

        VBox bookingCard = new VBox();
        bookingCard.getStyleClass().addAll("card");

        bookingCard.getChildren().addAll(
                dateBox,
                vehicleBox,
                mechanicLink
        );
        return bookingCard;
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