package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateCustomer {

    private final GarageSystem garageSystem = new GarageSystem();
    private final Stage popupStage;
    private final Customer customer;

    public CreateCustomer(Stage popupStage, Customer customer) {
        this.popupStage = popupStage;
        this.customer = customer;
    }

    public Parent show() {
        boolean editMode = customer != null;

        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(editMode
                ? LanguageManager.getString("customer.form.title.edit")
                : LanguageManager.getString("customer.form.title.create"));
        title.getStyleClass().add("form-title");

        Label nameLabel = new Label(LanguageManager.getString("customer.form.name"));
        nameLabel.getStyleClass().add("form-field-label");
        TextField nameField = new TextField();
        nameField.setPromptText(LanguageManager.getString("customer.form.name.prompt"));
        VBox nameBox = new VBox(nameLabel, nameField);
        nameBox.getStyleClass().add("form-field-container");

        Label phoneLabel = new Label(LanguageManager.getString("customer.form.phone"));
        phoneLabel.getStyleClass().add("form-field-label");
        TextField phoneField = new TextField();
        phoneField.setPromptText(LanguageManager.getString("customer.form.phone.prompt"));
        VBox phoneBox = new VBox(phoneLabel, phoneField);
        phoneBox.getStyleClass().add("form-field-container");

        Label emailLabel = new Label(LanguageManager.getString("customer.form.email"));
        emailLabel.getStyleClass().add("form-field-label");
        TextField emailField = new TextField();
        emailField.setPromptText(LanguageManager.getString("customer.form.email.prompt"));
        VBox emailBox = new VBox(emailLabel, emailField);
        emailBox.getStyleClass().add("form-field-container");

        if (editMode) {
            nameField.setText(customer.getName());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail());
        }

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button submitBtn = new Button(editMode
                ? LanguageManager.getString("customer.form.submit.edit")
                : LanguageManager.getString("customer.form.submit.create"));
        submitBtn.getStyleClass().add("confirm-btn");
        Button cancelBtn = new Button(LanguageManager.getString("customer.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");

        submitBtn.disableProperty().bind(
                nameField.textProperty().isEmpty()
        );

        submitBtn.setOnAction(e -> {
            errorLabel.setText("");

            String nameValue = nameField.getText().trim();

            if (nameValue.isEmpty()) {
                errorLabel.setText(LanguageManager.getString("customer.form.error.nameRequired"));
                return;
            }

            String phoneValue = phoneField.getText().trim();
            String emailValue = emailField.getText().trim();

            if (editMode) {
                customer.setName(nameValue);
                customer.setPhone(phoneValue);
                customer.setEmail(emailValue);
                System.out.println("Calling ViewManager.showCustomers() to refresh list after edit");
            } else {
                garageSystem.createCustomer(nameValue, phoneValue, emailValue);
                System.out.println("Calling ViewManager.showCustomers() to refresh list after create");
            }

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(submitBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                title,
                nameBox,
                phoneBox,
                emailBox,
                buttonBox, errorLabel
        );

        return root;
    }
}