package com.wac.autocore.view.components;

import com.wac.autocore.controller.CustomerController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UiResultHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateCustomer {

    private final CustomerController customerController = ViewManager.getInstance().getControllers().customers();
    private final UiResultHandler uiResultHandler = new UiResultHandler();

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

        Label explanation = new Label(LanguageManager.getString("customer.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMinWidth(0);
        explanation.getStyleClass().add("form-explanation");

        VBox headerBox = new VBox(title, explanation);

        TextField nameField = new TextField();
        nameField.setPromptText(LanguageManager.getString("customer.form.name.prompt"));
        VBox nameBox = new FormFieldContainer(LanguageManager.getString("customer.form.name"), nameField, true);

        TextField phoneField = new TextField();
        phoneField.setPromptText(LanguageManager.getString("customer.form.phone.prompt"));
        VBox phoneBox = new FormFieldContainer(LanguageManager.getString("customer.form.phone"), phoneField, false);

        TextField emailField = new TextField();
        emailField.setPromptText(LanguageManager.getString("customer.form.email.prompt"));
        VBox emailBox = new FormFieldContainer(LanguageManager.getString("customer.form.email"), emailField, false);

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
                        .or(phoneField.textProperty().isEmpty()
                                .and(emailField.textProperty().isEmpty()))
        );

        submitBtn.setOnAction(e -> {
            errorLabel.setText("");

            String nameValue = nameField.getText().trim();

            if (nameValue.isBlank()) {
                errorLabel.setText(LanguageManager.getString("customer.form.error.nameRequired"));
                return;
            }

            String phoneValue = phoneField.getText().trim();
            String emailValue = emailField.getText().trim();

            if(phoneValue.isBlank() && emailValue.isBlank()){
                errorLabel.setText(LanguageManager.getString("customer.form.error.phoneOrEmailRequired"));
                return;
            }

            if (editMode) {
                customer.setName(nameValue);
                customer.setPhone(phoneValue);
                customer.setEmail(emailValue);
                popupStage.close();
            } else {
                Result<Customer> savedCustomerResult = uiResultHandler.handle(
                        customerController.createCustomer(nameValue, phoneValue, emailValue)
                );
                if(savedCustomerResult.isSuccess()){
                    popupStage.close();

                    boolean createVehicle = AppDialog.showConfirm(
                            LanguageManager.getString("customer.form.dialog.success.title"),
                            LanguageManager.getString("customer.create.vehicle.prompt"),
                            String.format(
                                    LanguageManager.getString("customer.create.vehicle.message"),
                                    savedCustomerResult.value().getId()
                            )
                    );
                    if(createVehicle){
                        ViewManager.getInstance().showCreateVehiclePopup(savedCustomerResult.value().getId());
                    }
                }
            }
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(submitBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                headerBox,
                nameBox,
                phoneBox,
                emailBox,
                buttonBox, errorLabel
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        return scrollPane;
    }
}
