package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
        return createForm(null);
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

        return createForm(selectedCustomer);
    }

    private Parent createForm(Customer preselectedCustomer){
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("vehicle.form.title"));
        title.getStyleClass().add("form-title");

        Label customerLabel = new Label(LanguageManager.getString("vehicle.form.customer.label"));
        customerLabel.getStyleClass().add("form-field-label");

        ObjectProperty<Customer> selectedCustomer = new SimpleObjectProperty<>();
        VBox customerBox;
        if(preselectedCustomer == null){
            CustomerSelection customerSelection = new CustomerSelection(garageSystem,
                    selectedCustomer::set);
            customerBox = new VBox(customerSelection);
        } else {
            selectedCustomer.set(preselectedCustomer);
            CustomerCard customerCard = new CustomerCard(preselectedCustomer);
            customerCard.getStyleClass().addAll("compact");
            customerBox = new VBox(customerCard);
        }

        TextField regField = new TextField();
        regField.setPromptText(LanguageManager.getString("vehicle.form.regNumber.prompt"));
        VBox regBox = getFormFieldContainer("vehicle.form.regNumber.label", regField);

        TextField brandField = new TextField();
        brandField.setPromptText(LanguageManager.getString("vehicle.form.brand.prompt"));
        VBox brandBox = getFormFieldContainer("vehicle.form.brand.label", brandField);

        TextField modelField = new TextField();
        modelField.setPromptText(LanguageManager.getString("vehicle.form.model.prompt"));
        VBox modelBox = getFormFieldContainer("vehicle.form.model.label", modelField);

        TextField yearField = new TextField();
        yearField.setPromptText(LanguageManager.getString("vehicle.form.year.prompt"));
        VBox yearBox = getFormFieldContainer("vehicle.form.year.label", yearField);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button createBtn = new Button(LanguageManager.getString("vehicle.form.submit"));
        createBtn.getStyleClass().add("confirm-btn");

        createBtn.disableProperty().bind(
                regField.textProperty().isEmpty()
                        .or(brandField.textProperty().isEmpty())
                        .or(modelField.textProperty().isEmpty())
                        .or(yearField.textProperty().isEmpty())
                        .or(selectedCustomer.isNull())
        );

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

            boolean successful = createVehicle(
                    registrationNumber,
                    brand,
                    model,
                    year,
                    selectedCustomer.get(),
                    errorLabel);
            if (!successful)
                return;

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(createBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                title,
                customerBox,
                regBox,
                brandBox,
                modelBox,
                yearBox,
                errorLabel,
                buttonBox
        );

        return root;
    }

    private static VBox getFormFieldContainer(String key, TextField field) {
        Label yearLabel = new Label(LanguageManager.getString(key));
        yearLabel.getStyleClass().add("form-field-label");
        VBox yearBox = new VBox(yearLabel, field);
        yearBox.getStyleClass().add("form-field-container");
        return yearBox;
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
            //TODO what would be wrong? GarageSystem need checks and then handle with try/catch and AppDialog
            AppDialog.showError(
                    LanguageManager.getString("vehicle.form.error.failed"),
                    LanguageManager.getString("vehicle.form.error.failed"),
                    LanguageManager.getString("vehicle.form.error.failed")
            );
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
            ViewManager.getInstance().showCreateBooking(vehicle.getId());
        }

        return true;
    }
}