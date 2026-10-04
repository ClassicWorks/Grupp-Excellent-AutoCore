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

        Label nameLabel = new Label(
                LanguageManager.getString("customer.name")
        );
        nameLabel.getStyleClass().add("form-field-label");

        nameField.setText(customer.getName());
        VBox nameBox = new VBox(nameLabel, nameField);
        nameBox.getStyleClass().add("form-field-container");


        Label phoneLabel = new Label(
                LanguageManager.getString("customer.phone")
        );
        phoneLabel.getStyleClass().add("form-field-label");

        phoneField.setText(customer.getPhone());
        VBox phoneBox = new VBox(phoneLabel, phoneField);
        phoneBox.getStyleClass().add("form-field-container");


        Label emailLabel = new Label(
                LanguageManager.getString("customer.email")
        );
        emailLabel.getStyleClass().add("form-field-label");

        emailField.setText(customer.getEmail());
        VBox emailBox = new VBox(emailLabel, emailField);
        emailBox.getStyleClass().add("form-field-container");


        Label vipLabel = new Label(
                customer.isVip()
                        ? LanguageManager.getString("customer.vip.yes")
                        : LanguageManager.getString("customer.vip.no")
        );

        VBox vipBox = new VBox(vipLabel);
        vipBox.getStyleClass().add("form-field-container");


        errorLabel.getStyleClass().add("error-label");


        VBox fields = new VBox(
                nameBox,
                phoneBox,
                emailBox,
                vipBox,
                errorLabel
        );

        HBox actionableButtons = new HBox(createSaveBtn());
        actionableButtons.getStyleClass().add("btn-container");

        this.getStyleClass().add("details-container");
        this.setTop(idLabel);
        this.setCenter(fields);
        this.setBottom(actionableButtons);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("customer.save"));
        saveBtn.getStyleClass().add("confirm-btn");

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