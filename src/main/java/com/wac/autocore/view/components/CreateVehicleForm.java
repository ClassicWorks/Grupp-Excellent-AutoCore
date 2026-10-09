package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;


public class CreateVehicleForm {

    private final GarageSystem garageSystem;
    private final Stage popupStage;

    public CreateVehicleForm(GarageSystem garageSystem, Stage popUpStage) {
        this.garageSystem = garageSystem;
        this.popupStage = popUpStage;
    }

    public Parent show() {
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("vehicle.form.title"));
        title.getStyleClass().add("form-title");

        VBox regBox = new VBox();
        Label regLabel = new Label(LanguageManager.getString("vehicle.form.regNumber.label"));
        regLabel.getStyleClass().add("form-field-label");
        TextField regField = new TextField();
        regField.setPromptText(LanguageManager.getString("vehicle.form.regNumber.prompt"));
        regBox.getChildren().addAll(regLabel, regField);
        regBox.getStyleClass().add("form-field-container");

        Label brandLabel = new Label(LanguageManager.getString("vehicle.form.brand.label"));
        brandLabel.getStyleClass().add("form-field-label");
        TextField brandField = new TextField();
        brandField.setPromptText(LanguageManager.getString("vehicle.form.brand.prompt"));
        VBox brandBox = new VBox(brandLabel, brandField);
        brandBox.getStyleClass().add("form-field-container");

        Label modelLabel = new Label(LanguageManager.getString("vehicle.form.model.label"));
        modelLabel.getStyleClass().add("form-field-label");
        TextField modelField = new TextField();
        modelField.setPromptText(LanguageManager.getString("vehicle.form.model.prompt"));
        VBox modelBox = new VBox(modelLabel, modelField);
        modelBox.getStyleClass().add("form-field-container");

        Label yearLabel = new Label(LanguageManager.getString("vehicle.form.year.label"));
        yearLabel.getStyleClass().add("form-field-label");
        TextField yearField = new TextField();
        yearField.setPromptText(LanguageManager.getString("vehicle.form.year.prompt"));
        VBox yearBox = new VBox(yearLabel, yearField);
        yearBox.getStyleClass().add("form-field-container");

        Label customerLabel = new Label(LanguageManager.getString("vehicle.form.customer.label"));
        customerLabel.getStyleClass().add("form-field-label");
        ObservableList<Customer> customers =
                FXCollections.observableArrayList(garageSystem.getCustomers());

        ComboBox<Customer> customerComboBox = new ComboBox<>(customers);
        customerComboBox.setPromptText(LanguageManager.getString("vehicle.form.customer.prompt"));
        customerComboBox.setConverter(ComboBoxLabels.customer());
        VBox customerBox = new VBox(customerLabel, customerComboBox);
        customerBox.getStyleClass().add("form-field-container");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button createBtn = new Button(LanguageManager.getString("vehicle.form.submit"));
        createBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button(LanguageManager.getString("vehicle.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");

        createBtn.setOnAction(e -> {
            errorLabel.setText("");

            Customer selectedCustomer = customerComboBox.getValue();

            if (selectedCustomer == null) {
                errorLabel.setText(LanguageManager.getString("vehicle.form.error.noCustomer"));
                return;
            }

            String registrationNumber = regField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();

            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch(NumberFormatException ex) {
                errorLabel.setText(LanguageManager.getString("vehicle.form.error.yearInvalid"));
                return;
            }

            boolean successful = createVehicle(registrationNumber, brand, model, year, selectedCustomer, errorLabel);
            if (!successful)
                return;

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

    public Parent show(int customerId) {
        Optional<Customer> optionalCustomer = garageSystem.getCustomer(customerId);
        if(!optionalCustomer.isPresent()){
            AppDialog.showError(
                    LanguageManager.getString("vehicle.form.customerNotFound.title"),
                    LanguageManager.getString("vehicle.form.customerNotFound.message"),
                    String.format(
                            LanguageManager.getString("vehicle.form.customerNotFound.details"),
                            customerId
                    )
            );
            popupStage.close();
            return null;
        }
        Customer selectedCustomer = optionalCustomer.get();

        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("vehicle.form.title"));
        title.getStyleClass().add("form-title");

        CustomerCard customerCard = new CustomerCard(selectedCustomer);
        customerCard.getStyleClass().addAll("compact");

        VBox regBox = new VBox();
        Label regLabel = new Label(LanguageManager.getString("vehicle.form.regNumber.label"));
        regLabel.getStyleClass().add("form-field-label");
        TextField regField = new TextField();
        regField.setPromptText(LanguageManager.getString("vehicle.form.regNumber.prompt"));
        regBox.getChildren().addAll(regLabel, regField);
        regBox.getStyleClass().add("form-field-container");

        Label brandLabel = new Label(LanguageManager.getString("vehicle.form.brand.label"));
        brandLabel.getStyleClass().add("form-field-label");
        TextField brandField = new TextField();
        brandField.setPromptText(LanguageManager.getString("vehicle.form.brand.prompt"));
        VBox brandBox = new VBox(brandLabel, brandField);
        brandBox.getStyleClass().add("form-field-container");

        Label modelLabel = new Label(LanguageManager.getString("vehicle.form.model.label"));
        modelLabel.getStyleClass().add("form-field-label");
        TextField modelField = new TextField();
        modelField.setPromptText(LanguageManager.getString("vehicle.form.model.prompt"));
        VBox modelBox = new VBox(modelLabel, modelField);
        modelBox.getStyleClass().add("form-field-container");

        Label yearLabel = new Label(LanguageManager.getString("vehicle.form.year.label"));
        yearLabel.getStyleClass().add("form-field-label");
        TextField yearField = new TextField();
        yearField.setPromptText(LanguageManager.getString("vehicle.form.year.prompt"));
        VBox yearBox = new VBox(yearLabel, yearField);
        yearBox.getStyleClass().add("form-field-container");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button createBtn = new Button(LanguageManager.getString("vehicle.form.submit"));
        createBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button(LanguageManager.getString("vehicle.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");

        createBtn.setOnAction(e -> {
            errorLabel.setText("");

            String registrationNumber = regField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();

            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch(NumberFormatException ex) {
                errorLabel.setText(LanguageManager.getString("vehicle.form.error.yearInvalid"));
                return;
            }

            boolean successful = createVehicle(registrationNumber, brand, model, year, selectedCustomer, errorLabel);
            if (!successful)
                return;

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(createBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                title,
                customerCard,
                regBox,
                brandBox,
                modelBox,
                yearBox,
                errorLabel,
                buttonBox
        );

        return root;
    }

    private boolean createVehicle(String registrationNumber, String brand, String model, int year, Customer selectedCustomer, Label errorLabel) {
        //Maybe specific checks

        Vehicle vehicle = garageSystem.createVehicle(
                registrationNumber,
                brand,
                model,
                year,
                selectedCustomer.getId()
        );

        if (vehicle == null) {
            errorLabel.setText(LanguageManager.getString("vehicle.form.error.failed"));
            return false;
        }
        boolean createBooking = AppDialog.showConfirm(
                LanguageManager.getString("vehicle.create.success.title"),
                LanguageManager.getString("vehicle.create.booking.prompt"),
                String.format(
                        LanguageManager.getString("vehicle.create.booking.message"),
                        vehicle.getId()
                )
        );
        if(createBooking){
            System.out.println("Should call on ViewManager.createBooking or createWorkOrder");
            //ViewManager.getInstance().show
        }

        return true;
    }
}