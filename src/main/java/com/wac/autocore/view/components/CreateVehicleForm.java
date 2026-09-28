package com.wac.autocore.view.components;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class CreateVehicleForm {

    private final GarageSystem garageSystem = new GarageSystem();
    private final Stage popupStage;

    public CreateVehicleForm(Stage popUpStage) {
        this.popupStage = popUpStage;
    }

    public Parent show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Create new vehicle");

        TextField regField = new TextField();
        regField.setPromptText("Registration number");

        TextField brandField = new TextField();
        brandField.setPromptText("Brand");

        TextField modelField = new TextField();
        modelField.setPromptText("Model");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        ObservableList<Customer> customers = FXCollections.observableArrayList(Database.getCustomers());
        ComboBox<Customer> customerComboBox = new ComboBox<>(customers);
        customerComboBox.setPromptText("Choose customer");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button createBtn = new Button("Create vehicle");
        Button cancelBtn = new Button("Cancel");

        createBtn.setOnAction(e -> {
            errorLabel.setText("");

            Customer selectedCustomer = customerComboBox.getValue();

            if (selectedCustomer == null) {
                errorLabel.setText("You must select a customer.");
                return;
            }

            String registrationNumber = regField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();

            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch(NumberFormatException ex) {
                errorLabel.setText("Year must be a valid number.");
                return;
            }

            Vehicle vehicle = garageSystem.createVehicle(
                    registrationNumber,
                    brand,
                    model,
                    year,
                    selectedCustomer.getId()
            );

            if (vehicle == null) {
                errorLabel.setText("Could not create vehicle.");
                return;
            }

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(10, createBtn, cancelBtn);

        root.getChildren().addAll(
                title, regField, brandField, modelField, yearField,
                customerComboBox, buttonBox, errorLabel
        );

        return root;
    }
}
