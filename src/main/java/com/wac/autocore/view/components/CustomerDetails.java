package com.wac.autocore.view.components;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerDetails extends BorderPane {

    private final Customer customer;

    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final Label errorLabel = new Label();

    public CustomerDetails(Customer customer) {
        this.customer = customer;

        Label idLabel = new Label(String.format(LanguageManager.getString("customer.id"), customer.getId()));

        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());

        Label vipLabel = new Label(customer.isVip()
                ? LanguageManager.getString("customer.vip.yes")
                : LanguageManager.getString("customer.vip.no"));

        errorLabel.setStyle("-fx-text-fill: red;");

        VBox fields = new VBox(10,
                new Label(LanguageManager.getString("customer.name")),
                nameField,
                new Label(LanguageManager.getString("customer.phone")),
                phoneField,
                new Label(LanguageManager.getString("customer.email")),
                emailField,
                vipLabel,
                errorLabel
        );

        HBox actionableButtons = new HBox(20, createSaveBtn());

        this.setTop(idLabel);
        this.setCenter(fields);
        this.setBottom(actionableButtons);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("customer.save"));

        saveBtn.disableProperty().bind(nameField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");

            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setText(LanguageManager.getString("customer.error.nameEmpty"));
                return;
            }

            System.out.printf("Should call garageSystem.updateCustomer(%d, %s, %s, %s)%n",
                    customer.getId(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim());
        });
        return saveBtn;
    }
}