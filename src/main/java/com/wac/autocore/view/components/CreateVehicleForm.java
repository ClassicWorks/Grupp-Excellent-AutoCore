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
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label("Create new vehicle");
        title.getStyleClass().add("form-title");

        TextField regField = new TextField();
        regField.setPromptText("Registration number");
        VBox regBox = new VBox(regField);
        regBox.getStyleClass().add("form-field-container");

        TextField brandField = new TextField();
        brandField.setPromptText("Brand");
        VBox brandBox = new VBox(brandField);
        brandBox.getStyleClass().add("form-field-container");

        TextField modelField = new TextField();
        modelField.setPromptText("Model");
        VBox modelBox = new VBox(modelField);
        modelBox.getStyleClass().add("form-field-container");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        VBox yearBox = new VBox(yearField);
        yearBox.getStyleClass().add("form-field-container");

        ObservableList<Customer> customers =
                FXCollections.observableArrayList(Database.getCustomers());

        ComboBox<Customer> customerComboBox = new ComboBox<>(customers);
        customerComboBox.setPromptText("Choose customer");
        VBox customerBox = new VBox(customerComboBox);
        customerBox.getStyleClass().add("form-field-container");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button createBtn = new Button("Create vehicle");
        createBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("cancel-btn");

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

        HBox buttonBox = new HBox(createBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                title,
                regBox,
                brandBox,
                modelBox,
                yearBox,
                customerBox,
                buttonBox,
                errorLabel
        );

        return root;
    }
}
