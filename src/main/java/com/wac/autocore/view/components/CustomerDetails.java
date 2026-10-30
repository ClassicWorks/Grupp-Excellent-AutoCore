package com.wac.autocore.view.components;

import com.wac.autocore.controller.CustomerController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UiResultHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class CustomerDetails extends BorderPane {

    private final Customer customer;
    private final Runnable onSaved;
    private final CustomerController customerController;
    private final UiResultHandler uiResultHandler = new UiResultHandler();

    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final Label errorLabel = new Label();

    //read -> labels istället för fält
    public CustomerDetails(Customer customer, Runnable onSaved, Consumer<Integer> onDelete) {
        customerController = ViewManager.getInstance().getControllers().customers();
        AccessGuard accessGuard = ViewManager.getInstance().getAccessGuard();
        boolean canEdit = accessGuard.has(Permission.MANAGE_CUSTOMERS);
        this.customer = customer;
        this.onSaved = onSaved;

        Label idLabel = new Label(String.format(LanguageManager.getString("customer.id"), customer.getId()));
        idLabel.getStyleClass().add("details-title");

        nameField.setText(customer.getName());
        Label nameLabel = new Label(customer.getName());
        VBox nameBox = new FormFieldContainer(
                LanguageManager.getString("customer.name"),
                canEdit ? nameField : nameLabel,
                canEdit
        );

        phoneField.setText(customer.getPhone());
        Label phoneLabel = new Label(customer.getPhone());
        VBox phoneBox = new FormFieldContainer(
                LanguageManager.getString("customer.phone"),
                canEdit ? phoneField : phoneLabel,
                false
        );

        emailField.setText(customer.getEmail());
        Label emailLabel = new Label(customer.getEmail());
        VBox emailBox = new FormFieldContainer(
                LanguageManager.getString("customer.email"),
                canEdit ?  emailField : emailLabel,
                false
        );


        Label vipLabel = new Label(
                customer.isVip()
                        ? LanguageManager.getString("customer.vip.yes")
                        : LanguageManager.getString("customer.vip.no")
        );

        VBox vipBox = new VBox(vipLabel);
        vipBox.getStyleClass().add("form-field-container");


        errorLabel.getStyleClass().add("error-label");


        VBox fields = new VBox(
                //explanation,
                nameBox,
                phoneBox,
                emailBox,
                vipBox,
                errorLabel
        );
        if (canEdit) {
            Label explanation = new Label(LanguageManager.getString("customer.form.explanation"));
            explanation.setWrapText(true);
            explanation.getStyleClass().add("form-explanation");
            fields.getChildren().addFirst(explanation);
        }
        fields.getStyleClass().add("details-container");

        if(canEdit) {
            HBox confirmingButtons = new HBox(createSaveBtn());
            confirmingButtons.getStyleClass().add("btn-container");

            HBox destructiveButtons = new HBox(createDeleteBtn(onDelete));
            destructiveButtons.getStyleClass().add("btn-container");

            BorderPane actionableButtons = new BorderPane(null, null, destructiveButtons, null, confirmingButtons);
            this.setBottom(actionableButtons);
        }

        this.setTop(idLabel);
        this.setCenter(fields);
    }

    private Button createSaveBtn() {
        Button saveBtn = new Button(LanguageManager.getString("customer.save"));
        saveBtn.getStyleClass().add("confirm-btn");

        saveBtn.disableProperty().bind(nameField.textProperty().isEmpty());

        saveBtn.setOnAction(e -> {
            errorLabel.setText("");
            errorLabel.getStyleClass().remove("success");

            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setText(LanguageManager.getString("customer.error.nameEmpty"));
                return;
            }

            Result<Customer> updatedResult = uiResultHandler.handle(customerController.updateCustomer(
                    customer.getId(),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim()));

            if (!updatedResult.isSuccess()) {
                errorLabel.setText(LanguageManager.getString("customer.error.saveFailed"));
                return;
            }

            errorLabel.getStyleClass().add("success");
            errorLabel.setText(LanguageManager.getString("customer.saved"));
            onSaved.run();
        });
        return saveBtn;
    }

    private Button createDeleteBtn(Consumer<Integer> onDelete) {
        Button deleteBtn = new Button(LanguageManager.getString("customer.delete"));
        deleteBtn.getStyleClass().add("destroy-btn");
        deleteBtn.setOnAction(e -> onDelete.accept(customer.getId()));
        return deleteBtn;
    }
}
