package com.wac.autocore.view;

import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.VehicleCard;
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
    private Vehicle selectedVehicle;

    VBox vehicleResult = new VBox();
    VBox bookingInformation = new VBox();

    public CreateBookingView(GarageSystem garageSystem) {
        this.garageSystem = garageSystem;
    }

    public Parent show(){
        Label title = new Label("Create booking");
        VBox results = new VBox();
        Label searchFieldLabel = new Label("Choose customer");
        TextField searchField = new TextField();

        Label descriptionLabel = new Label("Describe the problem");
        TextArea descriptionField = new TextArea();

        Label mechanicLabel = new Label("Choose mechanic (optionally)");
        ObservableList<String> availableMechanics = FXCollections.observableArrayList();
        //TODO change to only show unbooked mechanics
        garageSystem.getMechanics().forEach(m ->
                availableMechanics.add(String.format("ID: %d - %s",
                        m.getId(), m.getName())));

        ComboBox<String> mechanicComboBox = new ComboBox<>(availableMechanics);
        mechanicComboBox.setPromptText("Choose mechanic");

        Button createBtn = new Button("Create booking");

        bookingInformation.setVisible(false);
        bookingInformation.getChildren().addAll(descriptionLabel, descriptionField, mechanicLabel, mechanicComboBox);

        createBtn.setOnAction(e -> {
            if (selectedVehicle != null) {
                int mechanicId = 0;
                String selectedMechanic = mechanicComboBox.getValue();
                if (selectedMechanic != null) {
                    for (Mechanic m : garageSystem.getMechanics()) {
                        //TODO very ugly code, need to fix
                        if (String.format("ID: %d - %s", m.getId(), m.getName()).equals(selectedMechanic)) {
                            mechanicId = m.getId();
                            break;
                        }
                    }
                }

                garageSystem.createBooking(
                        selectedVehicle.getId(),
                        LocalDate.now(),
                        descriptionField.getText(),
                        mechanicId
                );

                if (createBtn.getScene() != null && createBtn.getScene().getWindow() instanceof Stage) {
                    ((Stage) createBtn.getScene().getWindow()).close();
                }
            }
        });

        VBox layout = new VBox(title, searchFieldLabel, searchField, results, vehicleResult, bookingInformation, createBtn);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            results.getChildren().clear();

            garageSystem.getCustomers().stream()
                    .filter(c ->
                            c.getName().toLowerCase()
                                    .contains(newValue.toLowerCase())
                    ).forEach(c ->
                            results.getChildren().add(getResult(c))
                    );
        });

        return layout;
    }

    private Node getResult(Customer customer){
        Button resultBtn = new Button(String.format("ID: %d - %s", customer.getId(), customer.getName()));

        resultBtn.setOnAction(e -> {
            vehicleResult.getChildren().clear();
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
            selectedVehicle = vehicle;
            bookingInformation.setVisible(true);
        });
        return vehicleCard;
    }
}
