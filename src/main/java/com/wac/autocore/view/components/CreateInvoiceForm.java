package com.wac.autocore.view.components;

import com.wac.autocore.controller.InvoiceController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.util.ComboBoxLabels;
import com.wac.autocore.util.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class CreateInvoiceForm {

    private final InvoiceController invoiceController = ViewManager.getInstance().getControllers().invoices();
    private final Stage popupStage;

    public CreateInvoiceForm(Stage popupStage) {
        this.popupStage = popupStage;
    }

    public Parent show() {
        VBox root = new VBox();
        root.getStyleClass().add("form-container");

        Label title = new Label(LanguageManager.getString("invoice.form.title"));
        title.getStyleClass().add("form-title");
        Label explanation = new Label(LanguageManager.getString("invoice.form.explanation"));
        VBox headerBox = new VBox(title, explanation);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Result<List<WorkOrder>> workOrdersResult = invoiceController.getWorkOrdersThatCanBeInvoiced();
        List<WorkOrder> availableWorkOrders = List.of();
        if (workOrdersResult.isSuccess()) {
            availableWorkOrders = workOrdersResult.value();
        } else {
            errorLabel.setText(workOrdersResult.error());
        }

        //Chose workOrder
        ObservableList<WorkOrder> workOrderItems = FXCollections.observableArrayList(availableWorkOrders);

        Label workOrderLabel = new Label(LanguageManager.getString("invoice.form.workorder.label") + "(*)");
        workOrderLabel.getStyleClass().add("form-field-label");
        ComboBox<WorkOrder> workOrderComboBox = new ComboBox<>(workOrderItems);
        workOrderComboBox.setPromptText(LanguageManager.getString("invoice.form.workorder.prompt"));
        workOrderComboBox.setConverter(ComboBoxLabels.workOrder());

        VBox workOrderBox = new VBox(workOrderLabel, workOrderComboBox);
        workOrderBox.getStyleClass().add("form-field-container");

        //Discount code
        Label discountCodeLabel = new Label(LanguageManager.getString("invoice.form.discount.label"));
        discountCodeLabel.getStyleClass().add("form-field-label");
        TextField discountCodeField = new TextField();
        discountCodeField.setPromptText(LanguageManager.getString("invoice.form.discount.prompt"));

        VBox discountBox = new VBox(discountCodeField);
        discountBox.getStyleClass().add("form-field-container");

        Button createBtn = new Button(LanguageManager.getString("invoice.form.submit"));
        createBtn.getStyleClass().add("confirm-btn");

        Button cancelBtn = new Button(LanguageManager.getString("invoice.form.cancel"));
        cancelBtn.getStyleClass().add("cancel-btn");

        createBtn.disableProperty().bind(
                workOrderComboBox.valueProperty().isNotNull()
        );

        createBtn.setOnAction(e -> {
            errorLabel.setText("");

            WorkOrder selectedWorkOrder = workOrderComboBox.getValue();

            if (selectedWorkOrder == null) {
                errorLabel.setText(LanguageManager.getString("invoice.form.error.noWorkOrder"));
                return;
            }

            String discountCode = discountCodeField.getText().trim();

            Result<?> result = invoiceController.createInvoice(selectedWorkOrder.getId(), discountCode);
            if (result.isSuccess()) {
                popupStage.close();
            } else {
                errorLabel.setText(result.error());
            }
        });

        cancelBtn.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(createBtn, cancelBtn);
        buttonBox.getStyleClass().add("btn-container");

        root.getChildren().addAll(
                headerBox,
                workOrderBox,
                discountBox,
                buttonBox,
                errorLabel
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }
}