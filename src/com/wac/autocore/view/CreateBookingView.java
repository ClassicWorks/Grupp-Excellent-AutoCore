package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CreateBookingView {
    private GarageSystem garageSystem;
    private ObjectProperty<Vehicle> selectedVehicle;

    private VBox vehicleResult = new VBox();
    private VBox bookingInformation = new VBox();

    public CreateBookingView(GarageSystem garageSystem) {
        this.garageSystem = garageSystem;
        selectedVehicle = new SimpleObjectProperty<>();
    }

    public Parent show(){
        Label title = new Label("Create booking");
        VBox customerResults = new VBox();
        Label searchFieldLabel = new Label("Choose customer");
        TextField searchField = new TextField();

        Label descriptionLabel = new Label("Describe the problem");
        TextArea descriptionField = new TextArea();

        Label mechanicLabel = new Label("Choose mechanic (optionally)");
        //TODO change to only show unbooked mechanics
        ObservableList<Mechanic> availableMechanics = FXCollections.observableArrayList();
        availableMechanics.add(null);
        availableMechanics.addAll(garageSystem.getMechanics());
        ComboBox<Mechanic> mechanicComboBox = new ComboBox<>(availableMechanics);
        mechanicComboBox.setPromptText("Choose mechanic");

        Button createBtn = new Button("Create booking");
        Button cancelBtn = new Button("Cancel booking");
        HBox actionBtns = new HBox(cancelBtn, createBtn);

        bookingInformation.setVisible(false);
        bookingInformation.getChildren().addAll(descriptionLabel, descriptionField, mechanicLabel, mechanicComboBox);

        VBox layout = new VBox(title, searchFieldLabel, searchField, customerResults, vehicleResult, bookingInformation, actionBtns);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            customerResults.getChildren().clear();

            garageSystem.getCustomers().stream()
                    .filter(c ->
                            c.getName().toLowerCase()
                                    .contains(newValue.toLowerCase())
                    ).forEach(c ->
                            customerResults.getChildren().add(getResult(c))
                    );
        });

        createBtn.disableProperty().bind(
                selectedVehicle.isNull()
                        .or(descriptionField.textProperty().isEmpty())
        );

        createBtn.setOnAction(e -> {
            if (selectedVehicle.get() != null) {
                int mechanicId = 0;
                Mechanic mechanic = mechanicComboBox.getValue();
                if (mechanic != null) {
                    mechanicId = mechanic.getId();
                }

                garageSystem.createBooking(
                        selectedVehicle.get().getId(),
                        LocalDate.now(),
                        descriptionField.getText(),
                        mechanicId
                );

                if (createBtn.getScene() != null && createBtn.getScene().getWindow() instanceof Stage) {
                    ((Stage) createBtn.getScene().getWindow()).close();
                    //TODO call ViewManager.getInstance().showDialogBox() or something to confirm that booking has been created
                    ViewManager.getInstance().showBookings();
                }
            }
        });

        cancelBtn.setOnAction( e -> {
                    if (cancelBtn.getScene() != null && cancelBtn.getScene().getWindow() instanceof Stage) {
                        ((Stage) cancelBtn.getScene().getWindow()).close();
                    }
                }
        );

        return layout;
    }

    private Node getResult(Customer customer){
        Button resultBtn = new Button(String.format("ID: %d - %s", customer.getId(), customer.getName()));

        resultBtn.setOnAction(e -> {
            vehicleResult.getChildren().clear();
            selectedVehicle.set(null);
            garageSystem.getCustomersVehicle(customer.getId())
                    .forEach(v -> vehicleResult.getChildren().add(getResult(v)));
        });
        return resultBtn;
    }

    private Node getResult(Vehicle vehicle){
        //Info om bilen
        ImageView vehicleIcon = new ImageView(new Image("resources/imgs/car-solid.png"));
        vehicleIcon.setFitHeight(40);
        vehicleIcon.setFitWidth(40);

        Label regNumberLabel = new Label(vehicle.getRegistrationNumber());
        Label brandModelYearLabel = new Label(String.format("%s - %2s, %d",
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));
        VBox vehicleInfoBox = new VBox(regNumberLabel, brandModelYearLabel);

        HBox vehicleCard = new HBox(vehicleIcon, vehicleInfoBox);
        vehicleCard.setStyle("-fx-border-color: blue");
        vehicleCard.getStyleClass().add("vehicle-card");

        vehicleCard.setOnMouseClicked(e -> {
            selectedVehicle.set(vehicle);
            vehicleCard.setStyle("-fx-background-color: lightblue");
            bookingInformation.setVisible(true);
        });
        return vehicleCard;
    }
}
