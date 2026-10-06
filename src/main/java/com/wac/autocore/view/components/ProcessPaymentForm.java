package com.wac.autocore.view.components;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class ProcessPaymentForm {

    private final GarageSystem garageSystem = new GarageSystem();
    private final Stage popupStage;

    public ProcessPaymentForm(Stage popupStage) {
        this.popupStage = popupStage;
    }

    public Parent show() {
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("payment.form.title"));
        title.getStyleClass().add("form-title");

        List<Invoice> unpaidInvoices = garageSystem.getInvoices().stream()
                .filter(invoice -> !invoice.isPaid())
                .collect(Collectors.toList());

        ObservableList<Invoice> invoiceItems = FXCollections.observableArrayList(unpaidInvoices);

        ComboBox<Invoice> invoiceComboBox = new ComboBox<>(invoiceItems);
        invoiceComboBox.setPromptText(LanguageManager.getString("payment.form.invoice.prompt"));
        invoiceComboBox.setConverter(ComboBoxLabels.invoice());

        VBox invoiceBox = new VBox(invoiceComboBox);
        invoiceBox.getStyleClass().add("form-field-container");


        ObservableList<String> paymentTypes = FXCollections.observableArrayList("CARD", "SWISH", "CASH");

        ComboBox<String> paymentTypeComboBox = new ComboBox<>(paymentTypes);
        paymentTypeComboBox.setPromptText(LanguageManager.getString("payment.form.type.prompt"));
        paymentTypeComboBox.setConverter(ComboBoxLabels.paymentType());

        VBox paymentTypeBox = new VBox(paymentTypeComboBox);
        paymentTypeBox.getStyleClass().add("form-field-container");


        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");


        Button processBtn = new Button(LanguageManager.getString("payment.form.submit"));
        processBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button(LanguageManager.getString("payment.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");


        processBtn.setOnAction(e -> {
            errorLabel.setText("");

            Invoice selectedInvoice = invoiceComboBox.getValue();
            String selectedType = paymentTypeComboBox.getValue();

            if (selectedInvoice == null) {
                errorLabel.setText(LanguageManager.getString("payment.form.error.noInvoice"));
                return;
            }

            if (selectedType == null) {
                errorLabel.setText(LanguageManager.getString("payment.form.error.noType"));
                return;
            }

            Payment payment = garageSystem.processPayment(selectedInvoice.getId(), selectedType);

            if (payment == null || !payment.isSuccessful()) {
                errorLabel.setText(LanguageManager.getString("payment.form.error.failed"));
                return;
            }

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());


        HBox buttonBox = new HBox(processBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");


        root.getChildren().addAll(
                title,
                invoiceBox,
                paymentTypeBox,
                buttonBox,
                errorLabel
        );

        return root;
    }
}