package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
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

        Label title = new Label(editMode ? "Kundinformation" : "Skapa ny kund");

        Label nameLabel = new Label("Namn:");
        TextField nameField = new TextField();
        nameField.setPromptText("Namn Efternamn");

        Label phoneLabel = new Label("Telefon:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("07x-xxx xx xx");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("namn@exampel.com");

        if (editMode) {
            nameField.setText(customer.getName());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail());
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button submitBtn = new Button(editMode ? "Spara" : "Skapa kund");
        Button cancelBtn = new Button("Avbryt");

        submitBtn.setOnAction(e -> {
            errorLabel.setText("");

            String nameValue = nameField.getText().trim();

            if (nameValue.isEmpty()) {
                errorLabel.setText("Namn måste fyllas i.");
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