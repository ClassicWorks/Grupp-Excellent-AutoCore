package com.wac.autocore.view.components;

import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
        BorderPane root = new BorderPane();
        Label heading = new Label("Create Work Order");
        heading.getStyleClass().add("form-heading");
        root.setTop(heading);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> popupStage.close());

        //Check if required objects exist
        Optional<Booking> optionalBooking = garageSystem.getBooking(bookingId);
        if(!optionalBooking.isPresent()){
            root.setCenter(new Label("Error:Booking not found!"));
            root.setBottom(cancelBtn);
            return root;
        }
        Booking booking = optionalBooking.get();
        Optional<Vehicle> optionalVehicle = garageSystem.getVehicle(booking.getVehicleId());
        if(!optionalVehicle.isPresent()){
            root.setCenter(new Label("Error: Vehicle not found!"));
            root.setBottom(cancelBtn);
            return root;
        }
        Vehicle vehicle = optionalVehicle.get();

        Mechanic mechanic = garageSystem.getMechanic(booking.getMechanicId()).orElse(null);

        VBox bookingCard = createBookingCard(booking, vehicle, mechanic);

        Label mechanicLabel = new Label("Choose mechanic");

        ComboBox<Mechanic> mechanicComboBox = createMechanicComboBox();
        if(mechanic != null){
            mechanicComboBox.setValue(mechanic);
        }

        VBox serviceItemsBox = new VBox();
        List<CheckBox> serviceCheckBoxes = new ArrayList<>();
        for(ServiceItem service : garageSystem.getServiceItems()){
            CheckBox checkBox = new CheckBox(service.getName());
            checkBox.setUserData(service.getId());
            serviceCheckBoxes.add(checkBox);
            serviceItemsBox.getChildren().add(checkBox);
        }

        Button submitBtn = new Button("Create work order");
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

        root.setCenter(new VBox(
                bookingCard,
                mechanicLabel,
                mechanicComboBox,
                serviceItemsBox)
        );
        root.setBottom(new HBox(
                cancelBtn,
                submitBtn)
        );
        return root;
    }

    //TODO split BookingCard into BookingCardWithActionButtons and BookingCardSimple and use that instead
    private VBox createBookingCard(Booking booking, Vehicle vehicle, Mechanic mechanic){
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
        Hyperlink mechanicLink = new MechanicHyperLink(mechanic);

        VBox bookingCard = new VBox();
        bookingCard.setStyle("-fx-border-color: blue");
        bookingCard.getStyleClass().addAll("booking-card", "card");

        bookingCard.getChildren().addAll(
                dateBox,
                vehicleBox,
                mechanicLink
        );
        return bookingCard;
    }

    //TODO change to only show unbooked mechanics
    private ComboBox<Mechanic> createMechanicComboBox() {
        ObservableList<Mechanic> mechanics =
                FXCollections.observableArrayList();

        mechanics.addAll(garageSystem.getMechanics());

        ComboBox<Mechanic> comboBox =
                new ComboBox<>(mechanics);

        comboBox.setPromptText("Choose mechanic");

        return comboBox;
    }
}
