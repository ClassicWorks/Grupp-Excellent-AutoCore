package com.wac.autocore.view.components;

import com.wac.autocore.controller.PaymentController;
import com.wac.autocore.controller.InvoiceController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ProcessPaymentForm {

    private final InvoiceController invoiceController = ViewManager.getInstance().getControllers().invoices();
    private final PaymentController paymentController = ViewManager.getInstance().getControllers().payments();
    private final Stage popupStage;

    public ProcessPaymentForm(Stage popupStage) {
        this.popupStage = popupStage;
    }

    public Parent show() {
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("payment.form.title"));
        title.getStyleClass().add("form-title");

        Label explanation = new Label(LanguageManager.getString("general.form.explanation"));
        explanation.setWrapText(true);
        explanation.setMinWidth(0);
        explanation.getStyleClass().add("form-explanation");

        VBox headerBox = new VBox(title, explanation);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Result<List<Invoice>> invoicesResult = invoiceController.getUnpaidInvoices();
        List<Invoice> unpaidInvoices = List.of();
        if (invoicesResult.isSuccess()) {
            unpaidInvoices = invoicesResult.value();
        } else {
            errorLabel.setText(invoicesResult.error());
        }

        //Invoice
        ObservableList<Invoice> invoiceItems = FXCollections.observableArrayList(unpaidInvoices);

        ComboBox<Invoice> invoiceComboBox = new ComboBox<>(invoiceItems);
        invoiceComboBox.setPromptText(LanguageManager.getString("payment.form.invoice.prompt"));
        invoiceComboBox.setConverter(ComboBoxLabels.invoice());

        VBox invoiceBox = new FormFieldContainer(LanguageManager.getString("payment.form.invoice.label"), invoiceComboBox, true);

        //Payment type
        ObservableList<String> paymentTypes = FXCollections.observableArrayList("CARD", "SWISH", "CASH");

        ComboBox<String> paymentTypeComboBox = new ComboBox<>(paymentTypes);
        paymentTypeComboBox.setPromptText(LanguageManager.getString("payment.form.type.prompt"));
        paymentTypeComboBox.setConverter(ComboBoxLabels.paymentType());

        VBox paymentTypeBox = new FormFieldContainer(LanguageManager.getString("payment.form.type.label"), paymentTypeComboBox, true);

        //Buttons
        Button processBtn = new Button(LanguageManager.getString("payment.form.submit"));
        processBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button(LanguageManager.getString("payment.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");

        processBtn.disableProperty().bind(
                invoiceComboBox.valueProperty().isNull()
                    .or(paymentTypeComboBox.valueProperty().isNull())
        );

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

            Result<Payment> result = paymentController.processPayment(selectedInvoice.getId(), selectedType);

            if (!result.isSuccess()) {
                errorLabel.setText(result.error());
                return;
            }

            Payment payment = result.value();
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
                headerBox,
                invoiceBox,
                paymentTypeBox,
                buttonBox,
                errorLabel
        );
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
}