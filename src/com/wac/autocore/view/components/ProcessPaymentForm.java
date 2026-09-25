package com.wac.autocore.view.components;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.service.GarageSystem;
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
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Process payment");

        List<Invoice> unpaidInvoices = Database.getInvoices().stream()
                .filter(invoice -> !invoice.isPaid())
                .collect(Collectors.toList());

        ObservableList<Invoice> invoiceItems = FXCollections.observableArrayList(unpaidInvoices);
        ComboBox<Invoice> invoiceComboBox = new ComboBox<>(invoiceItems);
        invoiceComboBox.setPromptText("Choose invoice");

        ObservableList<String> paymentTypes = FXCollections.observableArrayList("CARD", "SWISH", "CASH");
        ComboBox<String> paymentTypeComboBox = new ComboBox<>(paymentTypes);
        paymentTypeComboBox.setPromptText("Choose payment type");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button processBtn = new Button("Process payment");
        Button cancelBtn = new Button("Cancel");

        processBtn.setOnAction(e -> {
            errorLabel.setText("");

            Invoice selectedInvoice = invoiceComboBox.getValue();
            String selectedType = paymentTypeComboBox.getValue();

            if (selectedInvoice == null) {
                errorLabel.setText("You must select an invoice.");
                return;
            }

            if (selectedType == null) {
                errorLabel.setText("You must select a payment type.");
                return;
            }

            Payment payment = garageSystem.processPayment(selectedInvoice.getId(), selectedType);

            if (payment == null || !payment.isSuccessful()) {
                errorLabel.setText("Payment failed.");
                return;
            }

            popupStage.close();
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(10, processBtn, cancelBtn);

        root.getChildren().addAll(title, invoiceComboBox, paymentTypeComboBox, buttonBox, errorLabel);

        return root;
    }
}