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

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label(editMode
                ? LanguageManager.getString("customer.form.title.edit")
                : LanguageManager.getString("customer.form.title.create"));

        Label nameLabel = new Label(LanguageManager.getString("customer.form.name"));
        TextField nameField = new TextField();
        nameField.setPromptText(LanguageManager.getString("customer.form.name.prompt"));

        Label phoneLabel = new Label(LanguageManager.getString("customer.form.phone"));
        TextField phoneField = new TextField();
        phoneField.setPromptText(LanguageManager.getString("customer.form.phone.prompt"));

        Label emailLabel = new Label(LanguageManager.getString("customer.form.email"));
        TextField emailField = new TextField();
        emailField.setPromptText(LanguageManager.getString("customer.form.email.prompt"));

        if (editMode) {
            nameField.setText(customer.getName());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail());
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button submitBtn = new Button(editMode
                ? LanguageManager.getString("customer.form.submit.edit")
                : LanguageManager.getString("customer.form.submit.create"));
        Button cancelBtn = new Button(LanguageManager.getString("customer.form.cancel"));

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

        HBox buttonBox = new HBox(10, submitBtn, cancelBtn);

        root.getChildren().addAll(
                title,
                nameLabel, nameField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                buttonBox, errorLabel
        );

        return root;
    }
}