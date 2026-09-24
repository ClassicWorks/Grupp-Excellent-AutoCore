package com.wac.autocore.view.components;

import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.model.Customer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CustomerForm {

    public static Node getForm(Customer customer){
        boolean editMode = customer != null;

        VBox form = new VBox(10);

        Label name = new Label("Namn:");
        TextField nameField = new TextField();
        nameField.setPromptText("Namn Efternamn");

        Label phoneLabel = new Label("Telefon:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("07x-xxx xx xx");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("namn@exampel.com");

        if (editMode) {nameField.setText(customer.getName()); phoneField.setText(customer.getPhone()); emailField.setText(customer.getEmail());}

        Label errorLabel = new Label();
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button submitBtn = new Button(editMode ? "Spara" : "Skapa kund");

        submitBtn.setOnAction(e -> {
            String nameValue = nameField.getText().trim();

            if (nameValue.isEmpty()) {
                errorLabel.setText("Namn måste fyllas i.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            String phoneValue = phoneField.getText().trim();
            String emailValue = emailField.getText().trim();

            if (editMode) {
                customer.setName(nameValue);
                customer.setPhone(phoneValue);
                customer.setEmail(emailValue);
                System.out.println("Calling ViewManager.showCustomers() to refresh list after edit");
            } else {
                new GarageSystem().createCustomer(nameValue, phoneValue, emailValue);
                nameField.clear();
                phoneField.clear();
                emailField.clear();
                System.out.println("Calling ViewManager.showCustomers() to refresh list after create");
            }
        });

        form.getChildren().addAll(name, nameField, phoneLabel, phoneField, emailLabel, emailField, errorLabel, submitBtn);

        return   form;
    }
}
